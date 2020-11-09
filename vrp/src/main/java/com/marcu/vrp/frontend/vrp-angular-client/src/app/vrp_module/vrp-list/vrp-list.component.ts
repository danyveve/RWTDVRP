import {Component, OnInit} from '@angular/core';
import {VrpService} from "../services/vrp.service";
import {VrpInstance} from "../model/vrp-instance";
import {Router} from "@angular/router";
import {Observable, Subject} from "rxjs";
import {debounceTime, distinctUntilChanged, switchMap} from "rxjs/operators";

@Component({
  selector: 'app-vrp-list',
  templateUrl: './vrp-list.component.html',
  styleUrls: ['./vrp-list.component.scss']
})
export class VrpListComponent implements OnInit {
  vrps$: Observable<Array<VrpInstance>>;
  filter: string = "";
  sortedColumn: string = "";
  ascendant: boolean = true;
  private searchTerms = new Subject<string>();
  private isGoingAsync: boolean;
  private vrpsAsync$: Observable<Array<VrpInstance>>;


  constructor(
    private vrpService: VrpService,
    public router: Router
  ) {
  }

  ngOnInit(): void {
    this.vrps$ = this.vrpService.getVrpsFiltered(this.filter, this.sortedColumn, this.ascendant);
    this.isGoingAsync = false;

    this.vrpsAsync$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.vrpService.getVrpsFiltered(term, this.sortedColumn, this.ascendant)),
    );
  }

  radioChanged(value: any, isAsc: boolean) {
    this.sortedColumn = value;
    this.ascendant = isAsc;
    this.vrps$ = this.vrpService.getVrpsFiltered(this.filter, this.sortedColumn, this.ascendant);
    this.isGoingAsync = false;
  }

  async searchVrp(value: string) {
    if (!this.isGoingAsync) {
      this.isGoingAsync = true;
      this.vrps$ = this.vrpsAsync$;
      await this.delay(200);
    }
    this.filter = value;
    this.searchTerms.next(value);
  }


  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  secondsToString(seconds: number): string {
    let hours = Math.floor(seconds / 3600);
    seconds = seconds - hours * 3600;
    let minutes = Math.floor(seconds / 60);
    seconds = seconds - minutes * 60;
    return hours + "h " + minutes + "m " + seconds + "s";
  }
}
