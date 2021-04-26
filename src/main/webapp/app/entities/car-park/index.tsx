import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import CarPark from './car-park';
import CarParkDetail from './car-park-detail';
import CarParkUpdate from './car-park-update';
import CarParkDeleteDialog from './car-park-delete-dialog';

/* Main routes for car park. Only admin should have access to these routes. */
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={CarParkUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={CarParkUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={CarParkDetail} />
      <ErrorBoundaryRoute path={match.url} component={CarPark} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={CarParkDeleteDialog} />
  </>
);

export default Routes;
