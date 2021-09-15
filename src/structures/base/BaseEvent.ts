import Client from '../../client/client'

export default abstract class BaseEvent {
    [x: string]: any;
  
    constructor(
      private name: string,
    ) {
      this.name = name;
    }
  
    public getName(): any { return this.name; }
  
    public abstract exec(client: Client, ...args: any[]): Promise<void>;
  }