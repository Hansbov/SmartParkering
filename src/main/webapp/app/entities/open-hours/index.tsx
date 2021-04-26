import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import OpenHours from './open-hours';
import OpenHoursDetail from './open-hours-detail';
import OpenHoursUpdate from './open-hours-update';
import OpenHoursDeleteDialog from './open-hours-delete-dialog';

/* Main routes for open hours. Only admin should have access to these routes.*/
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={OpenHoursUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={OpenHoursUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={OpenHoursDetail} />
      <ErrorBoundaryRoute path={match.url} component={OpenHours} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={OpenHoursDeleteDialog} />
  </>
);

export default Routes;
