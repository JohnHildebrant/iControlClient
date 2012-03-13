/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.session;

import com.wizards.operations.icontrol.data.Manager;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hildebj
 */
@Stateless
public class ManagerFacade extends AbstractFacade<Manager> {
  @PersistenceContext(unitName = "iControlClientPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public ManagerFacade() {
    super(Manager.class);
  }
  
}
