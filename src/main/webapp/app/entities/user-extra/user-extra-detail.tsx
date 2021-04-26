import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './user-extra.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IUserExtraDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const UserExtraDetail = (props: IUserExtraDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { userExtraEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="userExtraDetailsHeading">
          <Translate contentKey="smartParkingApp.userExtra.detail.title">UserExtra</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{userExtraEntity.id}</dd>
          <dt>
            <span id="currentParkingSpot">
              <Translate contentKey="smartParkingApp.userExtra.currentParkingSpot">Current Parking Spot</Translate>
            </span>
          </dt>
          <dd>{userExtraEntity.currentParkingSpot}</dd>
          <dt>
            <span id="timeOfParking">
              <Translate contentKey="smartParkingApp.userExtra.timeOfParking">Time Of Parking</Translate>
            </span>
          </dt>
          <dd>
            {userExtraEntity.timeOfParking ? (
              <TextFormat value={userExtraEntity.timeOfParking} type="date" format={APP_DATE_FORMAT} />
            ) : null}
          </dd>
          <dt>
            <Translate contentKey="smartParkingApp.userExtra.parkingSpot">Parking Spot</Translate>
          </dt>
          <dd>{userExtraEntity.parkingSpot ? userExtraEntity.parkingSpot.id : ''}</dd>
          <dt>
            <Translate contentKey="smartParkingApp.userExtra.user">User</Translate>
          </dt>
          <dd>{userExtraEntity.user ? userExtraEntity.user.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/user-extra" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/user-extra/${userExtraEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ userExtra }: IRootState) => ({
  userExtraEntity: userExtra.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserExtraDetail);
