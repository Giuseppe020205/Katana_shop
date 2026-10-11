export interface UtenteAutenticato {
  id : number;
  username: string;
  email: string
  }
export interface AuthResponse{
  token: string;
  utente: UtenteAutenticato;
}
