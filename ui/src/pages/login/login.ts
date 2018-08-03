import {Component} from '@angular/core';
import {IonicPage, NavController, NavParams} from 'ionic-angular';
import {LoginService} from "../../utilities/LoginService";
import {HomePage} from "../home/home";
import {User} from "../../objects/User";

/**
 * Generated class for the LoginPage page.
 *
 * See https://ionicframework.com/docs/components/#navigation for more info on
 * Ionic pages and navigation.
 */

@IonicPage()
@Component({
  selector: 'page-login',
  templateUrl: 'login.html',
})
export class LoginPage {
  public username : string = "";
  public password: string = "";
  public usernameError : string = null;
  public passwordError : string = null;
  public error : string = null;

  constructor(public navCtrl: NavController, public navParams: NavParams, public loginService : LoginService) {
    this.loginService.isUserLoggedIn().subscribe(res=>{
      if(res)
        this.navCtrl.setRoot(HomePage);
    });
  }

  ionViewDidLoad() {
    console.log('ionViewDidLoad LoginPage');
  }

  public lostFocus(v:string):void{
    var u = 'username';
    var p = 'password';
    var r = '#f67575';
    var b = '#76a9f4';
    if(v === u) {
      document.getElementById(u).style.borderBottomColor = b;
      document.getElementById(u+'-label').style.color = b;
      if(!this.username) {
        document.getElementById(u).style.borderBottomColor = r;
        document.getElementById(u + '-label').style.color = r;
      }
    }if(v === p) {
      document.getElementById(p).style.borderBottomColor = b;
      document.getElementById(p+'-label').style.color = b;
      if(!this.password) {
        document.getElementById(p).style.borderBottomColor = r;
        document.getElementById(p + '-label').style.color = r;
      }
    }
  }

  public validate(key:string) : void {
    //TODO form validation is both signIn and lostFocus
  }

  public signIn(){
    // console.log(this.username+","+this.password);
    // usernameError = null; passwordError = null;
    // if(!username){
    //   usernameError = "Username is invalid";
    // }if(!password){
    //   passwordError = "Password is invalid";
    // }
    //TODO form validation here

    this.loginService.login(new User(this.username,this.password)).subscribe(res=>{
      // console.log('result='+res);
      this.navCtrl.setRoot(HomePage);
    },err=>{
      console.log('error='+err);
    });
  }
}
