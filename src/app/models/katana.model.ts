export interface Katana {
  id?: number;
  nome: string;
  tipo_acciaio: string;
  prezzo: number;
  giacenza_magazzino: number;
  immagine?: string | null;
  version?: number;
  inOfferta?: boolean;
  percentualeSconto?: number | null;
  prezzoScontato?: number | null;
  offertaFine?: string | null;
}
