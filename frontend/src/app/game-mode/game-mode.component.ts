import { NotificationService } from './../service/notification.service';
import { Component } from '@angular/core';
import { Router } from '@angular/router';
import { GameModeService } from '../service/game-mode.service';
import { NgZone } from '@angular/core';

@Component({
  selector: 'app-game-mode',
  standalone: true,
  imports: [],
  templateUrl: './game-mode.component.html',
  styleUrl: './game-mode.component.css'
})
export class GameModeComponent {
  notification: string = '';
  constructor(private router: Router, private gameModeService: GameModeService, private notificationService: NotificationService, private ngZone: NgZone) { }
  selectSingleplayer(){
    this.gameModeService.newSingleplayerGame().subscribe({
      next: (data) => {
        if(data){
          window.sessionStorage.removeItem('whiteSide');
          window.sessionStorage.setItem('whiteSide','WHITE');
          this.router.navigate(['/game']);
        }
        else{
          this.router.navigate(['/singUp']);
        }
      },
      error: (error) => {
        this.router.navigate(['/signUp']);
      }
    });
  }
  selectMultiplayer(){
    this.notificationService.getNotifications().subscribe(notification => {
      this.notification = notification;
      console.log(this.notification);
      window.sessionStorage.removeItem('whiteSide');
      window.sessionStorage.setItem('whiteSide',this.notification);
      this.removeWaitingScreenHTML();
      this.ngZone.run(() => {//without it error "Navigation triggered outside Angular zone, did you forget to call 'ngZone.run()'?" would be thrown
        this.router.navigate(['/game']);
      });
    });
    this.gameModeService.newtMultiplayerGame().subscribe({
      next: (data) => {
        if(data){
          this.generateWaitingScreenHTML();
          console.log("Searching for game");
        }
        else{
          console.log("Error");
          this.router.navigate(['/signUp']);
        }
      },
      error: (error) => {
        this.router.navigate(['/signUp']);
      }
    });
  }
  generateWaitingScreenHTML() {
    const WaitingScreenContainer = document.getElementById('notification-container');
    if(WaitingScreenContainer!=null){
      WaitingScreenContainer.classList.add("notification-container");
      WaitingScreenContainer.innerHTML = "Searching for game";
    }
  }
  removeWaitingScreenHTML(){
    const WaitingScreenContainer = document.getElementById('notification-container');
    if(WaitingScreenContainer!=null){
      WaitingScreenContainer.parentNode?.removeChild(WaitingScreenContainer);
    }
  }
}
