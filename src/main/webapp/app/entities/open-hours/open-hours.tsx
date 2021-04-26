import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, InputGroup, Col, Row, Table } from 'reactstrap';
import { AvForm, AvGroup, AvInput } from 'availity-reactstrap-validation';
import { Translate, translate, ICrudSearchAction, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getSearchEntities, getEntities } from './open-hours.reducer';
import { IOpenHours } from 'app/shared/model/open-hours.model';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOpenHoursProps extends StateProps, DispatchProps, RouteComponentProps<{ url: string }> {}

export const OpenHours = (props: IOpenHoursProps) => {
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

  const { openHoursList, match, loading } = props;
  return (
    <div>
      <h2 id="open-hours-heading" data-cy="OpenHoursHeading">
        <Translate contentKey="smartParkingApp.openHours.home.title">Open Hours</Translate>
        <div className="d-flex justify-content-end">
          <Button className="mr-2" color="info" onClick={handleSyncList} disabled={loading}>
            <FontAwesomeIcon icon="sync" spin={loading} />{' '}
            <Translate contentKey="smartParkingApp.openHours.home.refreshListLabel">Refresh List</Translate>
          </Button>
          <Link to={`${match.url}/new`} className="btn btn-primary jh-create-entity" id="jh-create-entity" data-cy="entityCreateButton">
            <FontAwesomeIcon icon="plus" />
            &nbsp;
            <Translate contentKey="smartParkingApp.openHours.home.createLabel">Create new Open Hours</Translate>
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
                  placeholder={translate('smartParkingApp.openHours.home.search')}
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
        {openHoursList && openHoursList.length > 0 ? (
          <Table responsive>
            <thead>
              <tr>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.id">ID</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.weekday">Weekday</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.openingHour">Opening Hour</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.closingHour">Closing Hour</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.date">Date</Translate>
                </th>
                <th>
                  <Translate contentKey="smartParkingApp.openHours.carPark">Car Park</Translate>
                </th>
                <th />
              </tr>
            </thead>
            <tbody>
              {openHoursList.map((openHours, i) => (
                <tr key={`entity-${i}`} data-cy="entityTable">
                  <td>
                    <Button tag={Link} to={`${match.url}/${openHours.id}`} color="link" size="sm">
                      {openHours.id}
                    </Button>
                  </td>
                  <td>{openHours.weekday}</td>
                  <td>{openHours.openingHour}</td>
                  <td>{openHours.closingHour}</td>
                  <td>{openHours.date ? <TextFormat type="date" value={openHours.date} format={APP_LOCAL_DATE_FORMAT} /> : null}</td>
                  <td>{openHours.carPark ? <Link to={`car-park/${openHours.carPark.id}`}>{openHours.carPark.id}</Link> : ''}</td>
                  <td className="text-right">
                    <div className="btn-group flex-btn-group-container">
                      <Button tag={Link} to={`${match.url}/${openHours.id}`} color="info" size="sm" data-cy="entityDetailsButton">
                        <FontAwesomeIcon icon="eye" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.view">View</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${openHours.id}/edit`} color="primary" size="sm" data-cy="entityEditButton">
                        <FontAwesomeIcon icon="pencil-alt" />{' '}
                        <span className="d-none d-md-inline">
                          <Translate contentKey="entity.action.edit">Edit</Translate>
                        </span>
                      </Button>
                      <Button tag={Link} to={`${match.url}/${openHours.id}/delete`} color="danger" size="sm" data-cy="entityDeleteButton">
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
              <Translate contentKey="smartParkingApp.openHours.home.notFound">No Open Hours found</Translate>
            </div>
          )
        )}
      </div>
    </div>
  );
};

const mapStateToProps = ({ openHours }: IRootState) => ({
  openHoursList: openHours.entities,
  loading: openHours.loading,
});

const mapDispatchToProps = {
  getSearchEntities,
  getEntities,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(OpenHours);
