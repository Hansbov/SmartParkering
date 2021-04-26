import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import ParkingSpot from './parking-spot';
import ParkingSpotDetail from './parking-spot-detail';
import ParkingSpotUpdate from './parking-spot-update';
import ParkingSpotDeleteDialog from './parking-spot-delete-dialog';

/* Main routes for parking-spot. Only admin should have access to these routes.*/
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={ParkingSpotUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={ParkingSpotUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={ParkingSpotDetail} />
      <ErrorBoundaryRoute path={match.url} component={ParkingSpot} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={ParkingSpotDeleteDialog} />
  </>
);

export default Routes;
