import { Component } from '@angular/core';
import { Facebook } from '@ionic-native/facebook/ngx';
import { Router } from '@angular/router';
import { NativeStorage } from '@ionic-native/native-storage/ngx';
import { LoadingController, AlertController, Platform } from '@ionic/angular';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
})
export class LoginComponent {

constructor(
    private fb: Facebook,
    private nativeStorage: NativeStorage,
    public loadingController: LoadingController,
    private router: Router,
    private platform: Platform,
    public alertController: AlertController
  ) { }

  async doFbLogin(){
    const loading = await this.loadingController.create({
      message: 'Please wait...'
    });
    this.presentLoading(loading);

    //the permissions your facebook app needs from the user
     const permissions = ["public_profile", "email", "user_friends"];

    this.fb.login(permissions).then(response => {
      let userId = response.authResponse.userID;
      //Getting name and email properties
      //Learn more about permissions in https://developers.facebook.com/docs/facebook-login/permissions

      this.fb.api("/me?fields=name,email", permissions).then(user => {
      	this.fb.api("/me/friends", permissions).then(friendData => {
      		user.picture = "https://graph.facebook.com/" + userId + "/picture?type=large";
			this.nativeStorage.setItem('facebook_user',
        	{
          		name: user.name,
          		email: user.email,
          		picture: user.picture,
          		friends: friendData.data
        	}).then(() => {
          		this.router.navigate(["/home"]);
          		loading.dismiss();
        	}, error => {
          		console.log(error);
          		loading.dismiss();
        	})
        }, error => {
          	console.log(error);
          	loading.dismiss();
        	})
        })
      })
  }

  async presentAlert() {
    const alert = await this.alertController.create({
       message: 'Cordova is not available on desktop. Please try this in a real device or in an emulator.',
       buttons: ['OK']
     });

    await alert.present();
  }

  async presentLoading(loading) {
    return await loading.present();
  }

}
