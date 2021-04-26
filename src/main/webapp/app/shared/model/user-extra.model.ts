import dayjs from 'dayjs';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { IUser } from 'app/shared/model/user.model';

export interface IUserExtra {
  id?: number;
  currentParkingSpot?: string | null;
  timeOfParking?: string | null;
  parkingSpot?: IParkingSpot | null;
  user?: IUser | null;
}

export const defaultValue: Readonly<IUserExtra> = {};
