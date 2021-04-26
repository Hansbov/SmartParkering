import { IOpenHours } from 'app/shared/model/open-hours.model';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { IAddress } from 'app/shared/model/address.model';

export interface ICarPark {
  id?: number;
  name?: string | null;
  owner?: string | null;
  openHours?: IOpenHours[] | null;
  parkingSpots?: IParkingSpot[] | null;
  address?: IAddress | null;
}

export const defaultValue: Readonly<ICarPark> = {};
