import {Component, OnInit} from '@angular/core';
import {Observable, Subject} from "rxjs";
import {Driver} from "../model/driver";
import {Router} from "@angular/router";
import {DriverService} from "../services/driver.service";
import {debounceTime, distinctUntilChanged, switchMap} from "rxjs/operators";

@Component({
  selector: 'app-driver-list',
  templateUrl: './driver-list.component.html',
  styleUrls: ['./driver-list.component.scss']
})
export class DriverListComponent implements OnInit {
  drivers$: Observable<Array<Driver>>;
  filter: string = "";
  sortedColumn: string = "";
  ascendant: boolean = true;
  private searchTerms = new Subject<string>();
  private isGoingAsync: boolean;
  private driversAsync$: Observable<Array<Driver>>;

  constructor(
    private driverService: DriverService,
    public router: Router
  ) { }

  ngOnInit(): void {
    this.drivers$ = this.driverService.getDriversFiltered(this.filter, this.sortedColumn, this.ascendant);
    this.isGoingAsync = false;

    this.driversAsync$ = this.searchTerms.pipe(
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),

      // ignore new term if same as previous term
      distinctUntilChanged(),

      // switch to new search observable each time the term changes
      switchMap((term: string) => this.driverService.getDriversFiltered(term, this.sortedColumn, this.ascendant)),
    );
  }

  radioChanged(value: any, isAsc: boolean) {
    this.sortedColumn = value;
    this.ascendant = isAsc;
    this.drivers$ = this.driverService.getDriversFiltered(this.filter, this.sortedColumn, this.ascendant);
    this.isGoingAsync = false;
  }

  async searchDriver(value: string) {
    if (!this.isGoingAsync) {
      this.isGoingAsync = true;
      this.drivers$ = this.driversAsync$;
      await this.delay(200);
    }
    this.filter = value;
    this.searchTerms.next(value);
  }


  delay(ms: number) {
    return new Promise(resolve => setTimeout(resolve, ms));
  }

  deleteDriver(driver: Driver, $event) {
    $event.stopPropagation();
    this.driverService.deleteById(driver.id)
      .subscribe((result) => {
        this.radioChanged(this.sortedColumn, this.ascendant);
      }
      );
  }
}
