import { ICarPark } from 'app/shared/model/car-park.model';

export interface IParkingSpot {
  id?: number;
  accessableParking?: boolean | null;
  available?: boolean;
  floor?: number | null;
  carPark?: ICarPark | null;
}

export const defaultValue: Readonly<IParkingSpot> = {
  accessableParking: false,
  available: false,
};
