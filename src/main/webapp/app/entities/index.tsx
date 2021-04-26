import React from 'react';
import { Switch } from 'react-router-dom';

import CarPark from './car-park';
import Address from './address';
import ParkingSpot from './parking-spot';
import OpenHours from './open-hours';
import UserExtra from './user-extra';
import PrivateRoute from 'app/shared/auth/private-route';
import { AUTHORITIES } from 'app/config/constants';
/* jhipster-needle-add-route-import - JHipster will add routes here */

const Routes = ({ match }) => (
  <div>
    <Switch>
      {/* prettier-ignore */}
      <PrivateRoute path={`${match.url}car-park`} component={CarPark} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      <PrivateRoute path={`${match.url}address`} component={Address} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      <PrivateRoute path={`${match.url}parking-spot`} component={ParkingSpot} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      <PrivateRoute path={`${match.url}open-hours`} component={OpenHours} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      <PrivateRoute path={`${match.url}user-extra`} component={UserExtra} hasAnyAuthorities={[AUTHORITIES.ADMIN]} />
      {/* jhipster-needle-add-route-path - JHipster will add routes here */}
    </Switch>
  </div>
);

export default Routes;
