import {Injectable, Injector} from '@angular/core';
import {RollbarService} from "./rollbar";

@Injectable({
  providedIn: 'root'
})
export class LoggingService {

  constructor(private injector: Injector) { }

  logErrorMessageAndStacktrace(message: string, stack: string) {
    const rollbarService = this.injector.get(RollbarService);
    rollbarService.error(message, stack);
  }

  logError(error: Error) {
    const rollbarService = this.injector.get(RollbarService);
    rollbarService.error(error);
  }

  logSuccess(message: string) {
    const rollbarService = this.injector.get(RollbarService);
    rollbarService.info(message);
  }

  logDebug(message: string) {
    const rollbarService = this.injector.get(RollbarService);
    rollbarService.debug(message);
  }
}
