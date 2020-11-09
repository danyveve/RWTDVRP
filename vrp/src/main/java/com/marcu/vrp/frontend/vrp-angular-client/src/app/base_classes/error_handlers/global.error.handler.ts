import {ErrorHandler, Injectable, Injector} from '@angular/core';
import {HttpErrorResponse} from '@angular/common/http';
import {NotificationService} from "../logging_and_notifications/notification.service";
import {LoggingService} from "../logging_and_notifications/logging.service";
import {ErrorService} from "./error.service";
import {NotificationType} from "../logging_and_notifications/notification-type";
import {Router} from "@angular/router";

@Injectable({
  providedIn: 'root'
})
export class GlobalErrorHandler implements ErrorHandler {

  constructor(private injector: Injector,
              private router: Router) { }

  handleError(error: Error | HttpErrorResponse) {
    const errorService = this.injector.get(ErrorService);
    const logger = this.injector.get(LoggingService);
    const notifier = this.injector.get(NotificationService);

    let message;
    let stackTrace;

    console.log(error);

    if (error instanceof HttpErrorResponse) {
      // Server Error
      message = errorService.getServerMessage(error);
      stackTrace = errorService.getServerStack(error);
      notifier.addMessage(message, NotificationType.ERROR);
      if (error.status === 404) {
        this.router.navigateByUrl("/notFound");
      }
      if (error.status === 401) {
        this.router.navigateByUrl("/unauthorized");
      }
      if (error.status === 403) {
        this.router.navigateByUrl("/forbidden");
      }
    } else {
      // Client Error
      message = errorService.getClientMessage(error);
      stackTrace = errorService.getClientStack(error);
      notifier.addMessage(message, NotificationType.ERROR);
    }

    // Always log errors
    logger.logErrorMessageAndStacktrace(message, stackTrace);
  }
}
