import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
  HttpResponse
} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, tap} from 'rxjs/operators';
import {Injectable, Injector} from "@angular/core";
import {LoggingService} from "../logging_and_notifications/logging.service";

@Injectable({
  providedIn: 'root'
})
export class HttpErrorInterceptor implements HttpInterceptor {

  constructor(private injector: Injector) {}

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    return next.handle(request)
      .pipe(
        //retry(1), // this line will retry the request one more time, if it initially failed
        tap(response => {
          if (response instanceof HttpResponse){
            if(response.body && response.body.success) {
              const logger = this.injector.get(LoggingService);
              logger.logSuccess(response.body.success.message);
            }
          }
        }),
        catchError((error: HttpErrorResponse) => {
          const logger = this.injector.get(LoggingService);
          let errorMessage = '';
          if (error.error instanceof ErrorEvent) {
            // client-side error
            errorMessage = `Client Error Message: ${error.message}`;
          } else {
            // server-side error
            errorMessage = `Server Error Code: ${error.status}\nServer Error Message: ${error.message}`;
          }
          logger.logError(error.error);
          return throwError(error);
        })
      )
  }
}
