import { Utente } from './utente.model';
import {RigaOrdine} from './riga_ordine.model';

export interface Ordine {
    id?: number;
    dataOrdine?: string | Date;
    totale: number;
    utente?: Utente;
    righe?: RigaOrdine[];
    }
