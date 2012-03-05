/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.session;

import com.wizards.operations.icontrol.data.Pool;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author hildebj
 */
@Stateless
public class PoolFacade extends AbstractFacade<Pool> {
  @PersistenceContext(unitName = "iControlClientPU")
  private EntityManager em;

  @Override
  protected EntityManager getEntityManager() {
    return em;
  }

  public PoolFacade() {
    super(Pool.class);
  }
  
}
