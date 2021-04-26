import dayjs from 'dayjs';
import { ICarPark } from 'app/shared/model/car-park.model';

export interface IOpenHours {
  id?: number;
  weekday?: string | null;
  openingHour?: string | null;
  closingHour?: string | null;
  date?: string | null;
  carPark?: ICarPark | null;
}

export const defaultValue: Readonly<IOpenHours> = {};
