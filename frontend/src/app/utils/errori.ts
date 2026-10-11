import { HttpErrorResponse} from '@angular/common/http';

export function messaggioErrore(err: unknown, predefinito: string): string{
  if( err instanceof HttpErrorResponse){
    if( err.status===0){
      return 'server non raggiungibile. Controllare back-end';

      }
    return err.error?.messaggio ?? err.error?.message ?? predefinito;
  }
  return predefinito;
}
export function valoreInput(event: Event): string{
  return (event.target as HTMLInputElement).value;
}



