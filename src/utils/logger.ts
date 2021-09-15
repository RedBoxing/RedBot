import colors from 'colors'

export function info(message) {
    console.log(colors.cyan('[INFO]'), message);
}

export function error(message) {
    console.log(colors.red('[ERROR]'), message);
}

export function success(message) {
    console.log(colors.green('[SUCCESS]'), message);
}

export function auth(message) {
    console.log(colors.yellow('[AUTH]'), message);
}
