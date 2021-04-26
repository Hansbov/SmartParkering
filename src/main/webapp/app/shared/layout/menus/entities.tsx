import React from 'react';
import MenuItem from 'app/shared/layout/menus/menu-item';
import { Translate, translate } from 'react-jhipster';
import { NavDropdown } from './menu-components';

export const EntitiesMenu = props => (
  <NavDropdown
    icon="th-list"
    name={translate('global.menu.entities.main')}
    id="entity-menu"
    data-cy="entity"
    style={{ maxHeight: '80vh', overflow: 'auto' }}
  >
    <MenuItem icon="asterisk" to="/car-park">
      <Translate contentKey="global.menu.entities.carPark" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/address">
      <Translate contentKey="global.menu.entities.address" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/parking-spot">
      <Translate contentKey="global.menu.entities.parkingSpot" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/open-hours">
      <Translate contentKey="global.menu.entities.openHours" />
    </MenuItem>
    <MenuItem icon="asterisk" to="/user-extra">
      <Translate contentKey="global.menu.entities.userExtra" />
    </MenuItem>
    {/* jhipster-needle-add-entity-to-menu - JHipster will add entities to the menu here */}
  </NavDropdown>
);
