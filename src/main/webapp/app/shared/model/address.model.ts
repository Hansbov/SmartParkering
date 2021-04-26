import { ICarPark } from 'app/shared/model/car-park.model';

export interface IAddress {
  id?: number;
  streetAddress?: string;
  postalCode?: string | null;
  city?: string | null;
  carParks?: ICarPark[] | null;
}

export const defaultValue: Readonly<IAddress> = {};
