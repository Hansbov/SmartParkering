import React from 'react';
import { Switch } from 'react-router-dom';

import ErrorBoundaryRoute from 'app/shared/error/error-boundary-route';

import Address from './address';
import AddressDetail from './address-detail';
import AddressUpdate from './address-update';
import AddressDeleteDialog from './address-delete-dialog';

/* Main routes for parking-spot. Only admin should have access to these routes.*/
const Routes = ({ match }) => (
  <>
    <Switch>
      <ErrorBoundaryRoute exact path={`${match.url}/new`} component={AddressUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id/edit`} component={AddressUpdate} />
      <ErrorBoundaryRoute exact path={`${match.url}/:id`} component={AddressDetail} />
      <ErrorBoundaryRoute path={match.url} component={Address} />
    </Switch>
    <ErrorBoundaryRoute exact path={`${match.url}/:id/delete`} component={AddressDeleteDialog} />
  </>
);

export default Routes;
