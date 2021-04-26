import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { getEntities as getParkingSpots } from 'app/entities/parking-spot/parking-spot.reducer';
import { IUser } from 'app/shared/model/user.model';
import { getUsers } from 'app/modules/administration/user-management/user-management.reducer';
import { getEntity, updateEntity, createEntity, reset } from './user-extra.reducer';
import { IUserExtra } from 'app/shared/model/user-extra.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IUserExtraUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const UserExtraUpdate = (props: IUserExtraUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { userExtraEntity, parkingSpots, users, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/user-extra');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getParkingSpots();
    props.getUsers();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    values.timeOfParking = convertDateTimeToServer(values.timeOfParking);

    if (errors.length === 0) {
      const entity = {
        ...userExtraEntity,
        ...values,
        parkingSpot: parkingSpots.find(it => it.id.toString() === values.parkingSpotId.toString()),
        user: users.find(it => it.id.toString() === values.userId.toString()),
      };

      if (isNew) {
        props.createEntity(entity);
      } else {
        props.updateEntity(entity);
      }
    }
  };

  return (
    <div>
      <Row className="justify-content-center">
        <Col md="8">
          <h2 id="smartParkingApp.userExtra.home.createOrEditLabel" data-cy="UserExtraCreateUpdateHeading">
            <Translate contentKey="smartParkingApp.userExtra.home.createOrEditLabel">Create or edit a UserExtra</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : userExtraEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="user-extra-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="user-extra-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="currentParkingSpotLabel" for="user-extra-currentParkingSpot">
                  <Translate contentKey="smartParkingApp.userExtra.currentParkingSpot">Current Parking Spot</Translate>
                </Label>
                <AvField id="user-extra-currentParkingSpot" data-cy="currentParkingSpot" type="text" name="currentParkingSpot" />
              </AvGroup>
              <AvGroup>
                <Label id="timeOfParkingLabel" for="user-extra-timeOfParking">
                  <Translate contentKey="smartParkingApp.userExtra.timeOfParking">Time Of Parking</Translate>
                </Label>
                <AvInput
                  id="user-extra-timeOfParking"
                  data-cy="timeOfParking"
                  type="datetime-local"
                  className="form-control"
                  name="timeOfParking"
                  placeholder={'YYYY-MM-DD HH:mm'}
                  value={isNew ? displayDefaultDateTime() : convertDateTimeFromServer(props.userExtraEntity.timeOfParking)}
                />
              </AvGroup>
              <AvGroup>
                <Label for="user-extra-parkingSpot">
                  <Translate contentKey="smartParkingApp.userExtra.parkingSpot">Parking Spot</Translate>
                </Label>
                <AvInput id="user-extra-parkingSpot" data-cy="parkingSpot" type="select" className="form-control" name="parkingSpotId">
                  <option value="" key="0" />
                  {parkingSpots
                    ? parkingSpots.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <AvGroup>
                <Label for="user-extra-user">
                  <Translate contentKey="smartParkingApp.userExtra.user">User</Translate>
                </Label>
                <AvInput id="user-extra-user" data-cy="user" type="select" className="form-control" name="userId">
                  <option value="" key="0" />
                  {users
                    ? users.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/user-extra" replace color="info">
                <FontAwesomeIcon icon="arrow-left" />
                &nbsp;
                <span className="d-none d-md-inline">
                  <Translate contentKey="entity.action.back">Back</Translate>
                </span>
              </Button>
              &nbsp;
              <Button color="primary" id="save-entity" data-cy="entityCreateSaveButton" type="submit" disabled={updating}>
                <FontAwesomeIcon icon="save" />
                &nbsp;
                <Translate contentKey="entity.action.save">Save</Translate>
              </Button>
            </AvForm>
          )}
        </Col>
      </Row>
    </div>
  );
};

const mapStateToProps = (storeState: IRootState) => ({
  parkingSpots: storeState.parkingSpot.entities,
  users: storeState.userManagement.users,
  userExtraEntity: storeState.userExtra.entity,
  loading: storeState.userExtra.loading,
  updating: storeState.userExtra.updating,
  updateSuccess: storeState.userExtra.updateSuccess,
});

const mapDispatchToProps = {
  getParkingSpots,
  getUsers,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(UserExtraUpdate);
