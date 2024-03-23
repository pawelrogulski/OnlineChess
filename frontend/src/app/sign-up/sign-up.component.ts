import { Component } from '@angular/core';
import { DataService } from '../service/data.service';
import { Router } from '@angular/router';
import { FormBuilder } from '@angular/forms';


@Component({
  selector: 'app-sign-up',
  standalone: true,
  imports: [],
  templateUrl: './sign-up.component.html',
  styleUrl: './sign-up.component.css'
})
export class SignUpComponent {

  constructor(private formBuilder: FormBuilder, private dataService: DataService, private router: Router) { }

  signUp(username : string): void {
    this.dataService.signUp(username);
    this.router.navigate(['/gameMode']);
  }
}
