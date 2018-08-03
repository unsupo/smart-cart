import {Injectable} from "@angular/core";
import {Http, Headers} from "@angular/http";
import {Observable} from "rxjs/Observable";
import {ToastController} from "ionic-angular";
import {Utility} from "./Utility";
import 'rxjs/add/operator/map'
import {User} from "../objects/User";


@Injectable()
export class LoginService {
  public isLoggedIn: boolean = false;
  public user: User;

  constructor(private http: Http, public toast: ToastController) {
    this.isUserLoggedIn().subscribe(res => {
      if (res)
        this.toast.create({message: 'Welcome ' + this.user.username, duration: 2000}).present();
    });
  }

  public isUserLoggedIn(): Observable<boolean> {
    return new Observable(observer => {
      var u = localStorage.getItem(Utility.user);
      if (u) {
        this.user = JSON.parse(u);
        this.isTokenValid(this.user.token).subscribe(res => {
          if (res) {
            if (!this.user.username)
              this.getUserData(this.user.token).subscribe(
                res => {
                  this.loggedIn();
                  observer.next(true);
                }
              );
            else {
              this.loggedIn();
              observer.next(true);
            }
          } else {
            this.logOut();
            observer.next(false);
          }
        }, err=>{
            this.logOut();
            observer.next(false);
        });
      } else {
        this.logOut();
        observer.next(false);
      }

      observer.complete();
    });
  }

  private loggedIn(): void {
    this.isLoggedIn = true;
    localStorage.setItem(Utility.user, JSON.stringify(this.user));
  }


  public login(u: User): Observable<User> {
    this.user = u;
    var h = new Headers();
    h.set('username', u.username);
    h.set('password', u.password);
    // h.set('Access-Control-Allow-Origin','*');
    h.set('Content-Type', 'application/json;charset=UTF-8');
    return this.http.post(Utility.getHost().concat('/login'), "", {
      headers: h
    }).map(
      res => {
        this.isLoggedIn = true;
        this.user.token = res["_body"];
        localStorage.setItem(Utility.user, JSON.stringify(this.user));
        return this.user;
      }, err => {
        this.user = null;
        return null;
      }
    );
  }

  public isTokenValid(token: string): Observable<boolean> {
    var h = new Headers();
    h.set('token', token);
    return this.http.post(Utility.getHost().concat('/isValidToken'), "", {
      headers: h
    }).map(
      res => {
        return res["_body"] == 'true';
      }, err => {
        return false;
      }
    );
  }

  public getUserData(token: string): Observable<User> {
    var h = new Headers();
    h.set('token', token);
    return this.http.post(Utility.getHost().concat('/getUserData'), "", {
      headers: h
    }).map(
      res => {
        var json = JSON.parse(res["_body"]);
        var u = new User('', '');
        u.token = json['sessionId'];
        u.username = json['emailAddress'];
        u.lastName = json['lastName'];
        u.firstName = json['firstName'];
        u.locale = json['locale'];
        u.userGroup = json['userGroup'];
        u.imageUrl = json['imageUrl'];
        u.userId = json['userId'];

        this.user = u;
        localStorage.setItem(Utility.user, JSON.stringify(u));
        return u;
      }, err => {
        this.user = null;
        return null;
      }
    );
  }

  public logOut(): void {
    this.user = null;
    this.isLoggedIn = false;
    localStorage.clear();
    // localStorage.removeItem(Utility.user);
  }

  // No registering.  Do this with idm
  // public register(u : User) : Observable<User> {
  //   this.user = u;
  //   var h = new Headers();
  //   h.set('email',u.email);
  //   h.set('password',u.password);
  //   h.set('firstName',u.firstName);
  //   h.set('lastName',u.lastName);
  //   return this.http.post(Utility.getHost().concat('/register'),"",{
  //     headers : h
  //   }).map(
  //     res =>{
  //       this.user.token = res["_body"];
  //       this.isLoggedIn = true;
  //       localStorage.setItem(Utility.user,JSON.stringify(this.user));
  //       return this.user;
  //     }, err =>{
  //       this.user = null;
  //       return null;
  //     }
  //   );
  // }
}
