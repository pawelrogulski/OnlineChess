import { Component, OnInit } from '@angular/core';
import { Router, RouterOutlet } from '@angular/router';
import { ChessboardComponent } from "./chessboard/chessboard.component";
import { HttpClientModule } from '@angular/common/http';

@Component({
    selector: 'app-root',
    standalone: true,
    templateUrl: './app.component.html',
    styleUrl: './app.component.css',
    imports: [RouterOutlet, ChessboardComponent, HttpClientModule]
})
export class AppComponent implements OnInit {
  title: any;
  constructor(private router: Router) {}

  ngOnInit() {
    // forward to /display
    this.router.navigate(['/display']);
  }
}
