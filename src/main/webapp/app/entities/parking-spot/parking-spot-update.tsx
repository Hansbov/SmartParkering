import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col, Label } from 'reactstrap';
import { AvFeedback, AvForm, AvGroup, AvInput, AvField } from 'availity-reactstrap-validation';
import { Translate, translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { IRootState } from 'app/shared/reducers';

import { ICarPark } from 'app/shared/model/car-park.model';
import { getEntities as getCarParks } from 'app/entities/car-park/car-park.reducer';
import { getEntity, updateEntity, createEntity, reset } from './parking-spot.reducer';
import { IParkingSpot } from 'app/shared/model/parking-spot.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IParkingSpotUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const ParkingSpotUpdate = (props: IParkingSpotUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { parkingSpotEntity, carParks, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/parking-spot');
  };

  useEffect(() => {
    if (isNew) {
      props.reset();
    } else {
      props.getEntity(props.match.params.id);
    }

    props.getCarParks();
  }, []);

  useEffect(() => {
    if (props.updateSuccess) {
      handleClose();
    }
  }, [props.updateSuccess]);

  const saveEntity = (event, errors, values) => {
    if (errors.length === 0) {
      const entity = {
        ...parkingSpotEntity,
        ...values,
        carPark: carParks.find(it => it.id.toString() === values.carParkId.toString()),
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
          <h2 id="smartParkingApp.parkingSpot.home.createOrEditLabel" data-cy="ParkingSpotCreateUpdateHeading">
            <Translate contentKey="smartParkingApp.parkingSpot.home.createOrEditLabel">Create or edit a ParkingSpot</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : parkingSpotEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="parking-spot-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="parking-spot-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup check>
                <Label id="accessableParkingLabel">
                  <AvInput
                    id="parking-spot-accessableParking"
                    data-cy="accessableParking"
                    type="checkbox"
                    className="form-check-input"
                    name="accessableParking"
                  />
                  <Translate contentKey="smartParkingApp.parkingSpot.accessableParking">Accessable Parking</Translate>
                </Label>
              </AvGroup>
              <AvGroup check>
                <Label id="availableLabel">
                  <AvInput id="parking-spot-available" data-cy="available" type="checkbox" className="form-check-input" name="available" />
                  <Translate contentKey="smartParkingApp.parkingSpot.available">Available</Translate>
                </Label>
              </AvGroup>
              <AvGroup>
                <Label id="floorLabel" for="parking-spot-floor">
                  <Translate contentKey="smartParkingApp.parkingSpot.floor">Floor</Translate>
                </Label>
                <AvField id="parking-spot-floor" data-cy="floor" type="string" className="form-control" name="floor" />
              </AvGroup>
              <AvGroup>
                <Label for="parking-spot-carPark">
                  <Translate contentKey="smartParkingApp.parkingSpot.carPark">Car Park</Translate>
                </Label>
                <AvInput id="parking-spot-carPark" data-cy="carPark" type="select" className="form-control" name="carParkId">
                  <option value="" key="0" />
                  {carParks
                    ? carParks.map(otherEntity => (
                        <option value={otherEntity.id} key={otherEntity.id}>
                          {otherEntity.id}
                        </option>
                      ))
                    : null}
                </AvInput>
              </AvGroup>
              <Button tag={Link} id="cancel-save" to="/parking-spot" replace color="info">
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
  carParks: storeState.carPark.entities,
  parkingSpotEntity: storeState.parkingSpot.entity,
  loading: storeState.parkingSpot.loading,
  updating: storeState.parkingSpot.updating,
  updateSuccess: storeState.parkingSpot.updateSuccess,
});

const mapDispatchToProps = {
  getCarParks,
  getEntity,
  updateEntity,
  createEntity,
  reset,
};

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(ParkingSpotUpdate);
