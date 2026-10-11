import { Ordine } from './ordine.model';
import { Katana } from './katana.model';
export interface RigaOrdine {
    id?: number;
    ordine?: Ordine;
    katana: Katana;
    quantita: number;
    prezzoUnitario: number;
    subtotale?: number;
    }