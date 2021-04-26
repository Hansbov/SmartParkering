import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './parking-spot.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IParkingSpotDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ParkingSpotDetail = (props: IParkingSpotDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { parkingSpotEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="parkingSpotDetailsHeading">
          <Translate contentKey="smartParkingApp.parkingSpot.detail.title">ParkingSpot</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{parkingSpotEntity.id}</dd>
          <dt>
            <span id="accessableParking">
              <Translate contentKey="smartParkingApp.parkingSpot.accessableParking">Accessable Parking</Translate>
            </span>
          </dt>
          <dd>{parkingSpotEntity.accessableParking ? 'true' : 'false'}</dd>
          <dt>
            <span id="available">
              <Translate contentKey="smartParkingApp.parkingSpot.available">Available</Translate>
            </span>
          </dt>
          <dd>{parkingSpotEntity.available ? 'true' : 'false'}</dd>
          <dt>
            <span id="floor">
              <Translate contentKey="smartParkingApp.parkingSpot.floor">Floor</Translate>
            </span>
          </dt>
          <dd>{parkingSpotEntity.floor}</dd>
          <dt>
            <Translate contentKey="smartParkingApp.parkingSpot.carPark">Car Park</Translate>
          </dt>
          <dd>{parkingSpotEntity.carPark ? parkingSpotEntity.carPark.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/parking-spot" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/parking-spot/${parkingSpotEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ parkingSpot }: IRootState) => ({
  parkingSpotEntity: parkingSpot.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParkingSpotDetail);
