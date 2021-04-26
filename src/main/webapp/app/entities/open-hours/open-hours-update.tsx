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
import { getEntity, updateEntity, createEntity, reset } from './open-hours.reducer';
import { IOpenHours } from 'app/shared/model/open-hours.model';
import { convertDateTimeFromServer, convertDateTimeToServer, displayDefaultDateTime } from 'app/shared/util/date-utils';
import { mapIdList } from 'app/shared/util/entity-utils';

export interface IOpenHoursUpdateProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const OpenHoursUpdate = (props: IOpenHoursUpdateProps) => {
  const [isNew] = useState(!props.match.params || !props.match.params.id);

  const { openHoursEntity, carParks, loading, updating } = props;

  const handleClose = () => {
    props.history.push('/open-hours');
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
        ...openHoursEntity,
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
          <h2 id="smartParkingApp.openHours.home.createOrEditLabel" data-cy="OpenHoursCreateUpdateHeading">
            <Translate contentKey="smartParkingApp.openHours.home.createOrEditLabel">Create or edit a OpenHours</Translate>
          </h2>
        </Col>
      </Row>
      <Row className="justify-content-center">
        <Col md="8">
          {loading ? (
            <p>Loading...</p>
          ) : (
            <AvForm model={isNew ? {} : openHoursEntity} onSubmit={saveEntity}>
              {!isNew ? (
                <AvGroup>
                  <Label for="open-hours-id">
                    <Translate contentKey="global.field.id">ID</Translate>
                  </Label>
                  <AvInput id="open-hours-id" type="text" className="form-control" name="id" required readOnly />
                </AvGroup>
              ) : null}
              <AvGroup>
                <Label id="weekdayLabel" for="open-hours-weekday">
                  <Translate contentKey="smartParkingApp.openHours.weekday">Weekday</Translate>
                </Label>
                <AvField id="open-hours-weekday" data-cy="weekday" type="text" name="weekday" />
              </AvGroup>
              <AvGroup>
                <Label id="openingHourLabel" for="open-hours-openingHour">
                  <Translate contentKey="smartParkingApp.openHours.openingHour">Opening Hour</Translate>
                </Label>
                <AvField id="open-hours-openingHour" data-cy="openingHour" type="text" name="openingHour" />
              </AvGroup>
              <AvGroup>
                <Label id="closingHourLabel" for="open-hours-closingHour">
                  <Translate contentKey="smartParkingApp.openHours.closingHour">Closing Hour</Translate>
                </Label>
                <AvField id="open-hours-closingHour" data-cy="closingHour" type="text" name="closingHour" />
              </AvGroup>
              <AvGroup>
                <Label id="dateLabel" for="open-hours-date">
                  <Translate contentKey="smartParkingApp.openHours.date">Date</Translate>
                </Label>
                <AvField id="open-hours-date" data-cy="date" type="date" className="form-control" name="date" />
              </AvGroup>
              <AvGroup>
                <Label for="open-hours-carPark">
                  <Translate contentKey="smartParkingApp.openHours.carPark">Car Park</Translate>
                </Label>
                <AvInput id="open-hours-carPark" data-cy="carPark" type="select" className="form-control" name="carParkId">
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
              <Button tag={Link} id="cancel-save" to="/open-hours" replace color="info">
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
  openHoursEntity: storeState.openHours.entity,
  loading: storeState.openHours.loading,
  updating: storeState.openHours.updating,
  updateSuccess: storeState.openHours.updateSuccess,
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

export default connect(mapStateToProps, mapDispatchToProps)(OpenHoursUpdate);
