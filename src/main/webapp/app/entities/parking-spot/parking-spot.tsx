import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudSearchAction } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './parking-spot.reducer';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IParkingSpotProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const ParkingSpot = (props: IParkingSpotProps) => {
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

  const { parkingSpotList, match, loading } = props;
  return (
    <div>
      <h2 id="parking-spot-heading" data-cy="ParkingSpotHeading">
        <Translate contentKey="smartParkingApp.parkingSpot.home.title">Parking Spots</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="smartParkingApp.parkingSpot.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="smartParkingApp.parkingSpot.home.createLabel">Create new Parking Spot</Translate>
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
                  placeholder={translate('smartParkingApp.parkingSpot.home.search')}
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
        {parkingSpotList && parkingSpotList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="smartParkingApp.parkingSpot.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.parkingSpot.accessableParking">Accessable Parking</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.parkingSpot.available">Available</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.parkingSpot.floor">Floor</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.parkingSpot.carPark">Car Park</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {parkingSpotList.map((parkingSpot, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${parkingSpot.id}`} color="link" size="sm">
                      {parkingSpot.id}
                    </Button>
                  </td>
                  <td>{parkingSpot.accessableParking ? 'yes' : 'no'}</td>
                  <td>{parkingSpot.available ? 'true' : 'false'}</td>
                  <td>{parkingSpot.floor}</td>
                  <td>{parkingSpot.carPark ? <Link to={`car-park/${parkingSpot.carPark.id}`}>{parkingSpot.carPark.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${parkingSpot.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${parkingSpot.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${parkingSpot.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="smartParkingApp.parkingSpot.home.notFound">No Parking Spots found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ parkingSpot }: IRootState) => ({
  parkingSpotList: parkingSpot.entities,
  loading: parkingSpot.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParkingSpot);
