import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate, TextFormat } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './open-hours.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface IOpenHoursDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const OpenHoursDetail = (props: IOpenHoursDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { openHoursEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="openHoursDetailsHeading">
          <Translate contentKey="smartParkingApp.openHours.detail.title">OpenHours</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{openHoursEntity.id}</dd>
          <dt>
            <span id="weekday">
              <Translate contentKey="smartParkingApp.openHours.weekday">Weekday</Translate>
            </span>
          </dt>
          <dd>{openHoursEntity.weekday}</dd>
          <dt>
            <span id="openingHour">
              <Translate contentKey="smartParkingApp.openHours.openingHour">Opening Hour</Translate>
            </span>
          </dt>
          <dd>{openHoursEntity.openingHour}</dd>
          <dt>
            <span id="closingHour">
              <Translate contentKey="smartParkingApp.openHours.closingHour">Closing Hour</Translate>
            </span>
          </dt>
          <dd>{openHoursEntity.closingHour}</dd>
          <dt>
            <span id="date">
              <Translate contentKey="smartParkingApp.openHours.date">Date</Translate>
            </span>
          </dt>
          <dd>{openHoursEntity.date ? <TextFormat value={openHoursEntity.date} type="date" format={APP_LOCAL_DATE_FORMAT} /> : null}</dd>
          <dt>
            <Translate contentKey="smartParkingApp.openHours.carPark">Car Park</Translate>
          </dt>
          <dd>{openHoursEntity.carPark ? openHoursEntity.carPark.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/open-hours" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/open-hours/${openHoursEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ openHours }: IRootState) => ({
  openHoursEntity: openHours.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(OpenHoursDetail);
