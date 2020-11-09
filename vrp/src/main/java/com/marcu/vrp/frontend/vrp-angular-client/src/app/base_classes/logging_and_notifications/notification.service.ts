import {Injectable, NgZone} from '@angular/core';
import {MatSnackBar, MatSnackBarDismiss} from '@angular/material/snack-bar';
import {Observable} from "rxjs";
import {Pair} from "../../utils/pair";
import {NotificationType} from "./notification-type";

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private processingMessage = false;
  private messageQueue: Pair<string, NotificationType>[] = [];

  constructor(public snackBar: MatSnackBar,
              private ngZone: NgZone) {
  }

  private showSuccess(message: string): Observable<MatSnackBarDismiss> {
    return this.ngZone.run(() => {
      return this.snackBar.open(message,
        null,
        {duration: 1500}
        ).afterDismissed();
    });
  }

  private showError(message: string): Observable<MatSnackBarDismiss> {
    return this.ngZone.run(() => {
      return this.snackBar.open(message,
        'X',
        {duration: 1500}
        ).afterDismissed();
    });
  }

  private displaySnackbar(): void {
    const nextMessage = this.getNextMessage();

    if (!nextMessage) {
      this.processingMessage = false;
      return;
    }

    this.processingMessage = true;

    let snackbarDismiss: Observable<MatSnackBarDismiss> = nextMessage.right === NotificationType.SUCCESS ?
      this.showSuccess(nextMessage.left) :
      this.showError((nextMessage.left));

    snackbarDismiss.subscribe(() => {
      this.displaySnackbar();
    });
  }

  private getNextMessage(): Pair<string, NotificationType> | undefined {
    return this.messageQueue.length ? this.messageQueue.shift() : undefined;
  }

  addMessage(message: string, notificationType: NotificationType): void {
    this.messageQueue.push({left: message, right: notificationType} as Pair<string, NotificationType>);
    if (!this.processingMessage) {
      this.displaySnackbar();
    }
  }
}
