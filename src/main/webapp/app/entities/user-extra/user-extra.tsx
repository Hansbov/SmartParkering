import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudSearchAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './user-extra.reducer';
import { IUserExtra } from 'app/shared/model/user-extra.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IUserExtraProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const UserExtra = (props: IUserExtraProps) => {
  const [search, setSearch] = useState('');

  useEffect(() => {
    props.getEntities();
  }, []);

  const startSearching = () => {
    if (search) {
      props.getSearchEntities(search);
    }
  };

  const clear = () => {
    setSearch('');
    props.getEntities();
  };

  const handleSearch = event => setSearch(event.target.value);

  const handleSyncList = () => {
    props.getEntities();
  };

  const { userExtraList, match, loading } = props;
  return (
    <div>
      <h2 id="user-extra-heading" data-cy="UserExtraHeading">
        <Translate contentKey="smartParkingApp.userExtra.home.title">User Extras</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="smartParkingApp.userExtra.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="smartParkingApp.userExtra.home.createLabel">Create new User Extra</Translate>
          </Link>
        </div>
      </h2>
      <Row>
        <Col sm="12">
          <AvForm onSubmit={startSearching}>
            <AvGroup>
              <InputGroup>
                <AvInput
                  type="text"
                  name="search"
                  value={search}
                  onChange={handleSearch}
                  placeholder={translate('smartParkingApp.userExtra.home.search')}
                />
                <Button className="input-group-addon">
                  <FontAwesomeIcon icon="search" />
                </Button>
                <Button type="reset" className="input-group-addon" onClick={clear}>
                  <FontAwesomeIcon icon="trash" />
                </Button>
              </InputGroup>
            </AvGroup>
          </AvForm>
        </Col>
      </Row>
      <div className="table-responsive">
        {userExtraList && userExtraList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="smartParkingApp.userExtra.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.userExtra.currentParkingSpot">Current Parking Spot</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.userExtra.timeOfParking">Time Of Parking</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.userExtra.parkingSpot">Parking Spot</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.userExtra.user">User</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {userExtraList.map((userExtra, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${userExtra.id}`} color="link" size="sm">
                      {userExtra.id}
                    </Button>
                  </td>
                  <td>{userExtra.currentParkingSpot}</td>
                  <td>
                    {userExtra.timeOfParking ? <TextFormat type="date" value={userExtra.timeOfParking} format={APP_DATE_FORMAT} /> : null}
                  </td>
                  <td>
                    {userExtra.parkingSpot ? <Link to={`parking-spot/${userExtra.parkingSpot.id}`}>{userExtra.parkingSpot.id}</Link> : ''}
                  </td>
                  <td>{userExtra.user ? userExtra.user.id : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${userExtra.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${userExtra.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${userExtra.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
                        <FontAwesomeIcon icon="trash" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.delete">Delete</Translate>
                        </span>
                      </Button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        ) : (
          !loading && (
            <div className="alert alert-warning">
              <Translate contentKey="smartParkingApp.userExtra.home.notFound">No User Extras found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ userExtra }: IRootState) => ({
  userExtraList: userExtra.entities,
  loading: userExtra.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserExtra);
