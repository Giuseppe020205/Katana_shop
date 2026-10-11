import { Ruolo } from './ruolo.model';
import { Ordine } from './ordine.model';

export interface Utente {
    id?: number;
    username: string;
    password?: string;
    email: string;
    ruolo?: Ruolo | string;
    ordini?: Ordine[];
    }