export class User {
  constructor(username: string, password: string) {
    this.username = username;
    this.password = password;
  }

  userId : string;
    imageUrl : string;
    userGroup : string;
    locale : string;
    dateLastLogin : Date;
    username: string;
    password: string;
    firstName : string;
    lastName : string;
    token : string;
}
