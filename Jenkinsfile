pipeline {
    agent {
      kubernetes  {
            label 'jenkins-slave'
             defaultContainer 'jnlp'
      yaml """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: dind
    image: docker:dind
    securityContext:
      privileged: true
  - name: docker
    env:
    - name: DOCKER_HOST
      value: 127.0.0.1
    image: docker:latest
    command:
    - cat
    tty: true
  - name: tools
    image: argoproj/argo-cd-ci-builder:v1.0.0
    command:
    - cat
    tty: true
  - name: gradle
    image: gradle:7-jdk17-alpine
    command:
    - cat
    tty: true
"""
        }
    }
  environment {
      IMAGE_REPO = "redboxing/redbot"
  }
  stages {
    stage('Build') {
      environment {
        REGISTRY_CREDS = credentials('registry')
        REGISTRY_URL = "nexus-server:8082"
      }
      steps {
        container("gradle") {
          // Build the project
          sh "gradle clean build"
        }
        container('dind') {
          sh "echo ${env.GIT_COMMIT}"
          // Build new image
          sh "until docker container ls; do sleep 3; done && docker image build -t ${env.IMAGE_REPO}:${env.GIT_COMMIT} ."
          // Login to registry
          sh "docker login --username $REGISTRY_CREDS_USR --password $REGISTRY_CREDS_PSW $REGISTRY_URL"
          // Publish new image
          sh "docker image push ${env.REGISTRY_URL}/${env.IMAGE_REPO}:${env.GIT_COMMIT}"
        }
      }
    }
    stage('Deploy') {
      environment {
        GIT_CREDS = credentials('github')
        HELM_GIT_REPO_URL = "github.com/RedBoxing/helm-charts.git"
        GIT_REPO_EMAIL = 'thomas200574@gmail.com'
        GIT_REPO_BRANCH = "main"
      }
      steps {
        container('tools') {
            sh "git clone https://${env.HELM_GIT_REPO_URL}"
            sh "git config --global user.email ${env.GIT_REPO_EMAIL}"
             // install wq
            sh "wget https://github.com/mikefarah/yq/releases/download/v4.9.6/yq_linux_amd64.tar.gz"
            sh "tar xvf yq_linux_amd64.tar.gz"
            sh "mv yq_linux_amd64 /usr/bin/yq"
            sh "git checkout -b master"
          dir("helm-charts") {
              sh "git checkout ${env.GIT_REPO_BRANCH}"
            //install done
            dir("charts/redbot") {
                sh '''#!/bin/bash
                  echo $GIT_REPO_EMAIL
                  echo $GIT_COMMIT
                  ls -lth
                  yq eval '.image.repository = env(IMAGE_REPO)' -i values.yaml
                  yq eval '.image.tag = env(GIT_COMMIT)' -i values.yaml
                  cat values.yaml
                  pwd
                  git add values.yaml
                  git commit -m 'Triggered Build'
                  git push https://$GIT_CREDS_USR:$GIT_CREDS_PSW@github.com/$GIT_CREDS_USR/helm-charts.git
                '''
            }
          }
        }
      }
    }   
  }
}