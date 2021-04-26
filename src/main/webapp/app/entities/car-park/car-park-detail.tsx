import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import { Link, RouteComponentProps } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { Translate } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { IRootState } from 'app/shared/reducers';
import { getEntity } from './car-park.reducer';
import { APP_DATE_FORMAT, APP_LOCAL_DATE_FORMAT } from 'app/config/constants';

export interface ICarParkDetailProps extends StateProps, DispatchProps, RouteComponentProps<{ id: string }> {}

export const CarParkDetail = (props: ICarParkDetailProps) => {
  useEffect(() => {
    props.getEntity(props.match.params.id);
  }, []);

  const { carParkEntity } = props;
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="carParkDetailsHeading">
          <Translate contentKey="smartParkingApp.carPark.detail.title">CarPark</Translate>
        </h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">
              <Translate contentKey="global.field.id">ID</Translate>
            </span>
          </dt>
          <dd>{carParkEntity.id}</dd>
          <dt>
            <span id="name">
              <Translate contentKey="smartParkingApp.carPark.name">Name</Translate>
            </span>
          </dt>
          <dd>{carParkEntity.name}</dd>
          <dt>
            <span id="owner">
              <Translate contentKey="smartParkingApp.carPark.owner">Owner</Translate>
            </span>
          </dt>
          <dd>{carParkEntity.owner}</dd>
          <dt>
            <Translate contentKey="smartParkingApp.carPark.address">Address</Translate>
          </dt>
          <dd>{carParkEntity.address ? carParkEntity.address.id : ''}</dd>
        </dl>
        <Button tag={Link} to="/car-park" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.back">Back</Translate>
          </span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/car-park/${carParkEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" />{' '}
          <span className="d-none d-md-inline">
            <Translate contentKey="entity.action.edit">Edit</Translate>
          </span>
        </Button>
      </Col>
    </Row>
  );
};

const mapStateToProps = ({ carPark }: IRootState) => ({
  carParkEntity: carPark.entity,
});

const mapDispatchToProps = { getEntity };

type StateProps = ReturnType<typeof mapStateToProps>;
type DispatchProps = typeof mapDispatchToProps;

export default connect(mapStateToProps, mapDispatchToProps)(CarParkDetail);
