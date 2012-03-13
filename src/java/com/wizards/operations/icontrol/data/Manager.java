/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.wizards.operations.icontrol.data;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author hildebj
 */
@Entity
@Table(name = "manager")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "Manager.findAll", query = "SELECT m FROM Manager m"),
  @NamedQuery(name = "Manager.findById", query = "SELECT m FROM Manager m WHERE m.id = :id"),
  @NamedQuery(name = "Manager.findBySamaccountname", query = "SELECT m FROM Manager m WHERE m.samaccountname = :samaccountname"),
  @NamedQuery(name = "Manager.findByFullname", query = "SELECT m FROM Manager m WHERE m.fullname = :fullname")})
public class Manager implements Serializable {
  private static final long serialVersionUID = 1L;
  @Id
  @Basic(optional = false)
  @NotNull
  @Column(name = "id")
  private Integer id;
  @Size(max = 20)
  @Column(name = "samaccountname")
  private String samaccountname;
  @Size(max = 2147483647)
  @Column(name = "fullname")
  private String fullname;
  @JoinTable(name = "pool_manager", joinColumns = {
    @JoinColumn(name = "user_id", referencedColumnName = "id")}, inverseJoinColumns = {
    @JoinColumn(name = "pool_id", referencedColumnName = "id")})
  @ManyToMany
  private Collection<Pool> poolCollection;

  public Manager() {
  }

  public Manager(Integer id) {
    this.id = id;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getSamaccountname() {
    return samaccountname;
  }

  public void setSamaccountname(String samaccountname) {
    this.samaccountname = samaccountname;
  }

  public String getFullname() {
    return fullname;
  }

  public void setFullname(String fullname) {
    this.fullname = fullname;
  }

  @XmlTransient
  public Collection<Pool> getPoolCollection() {
    return poolCollection;
  }

  public void setPoolCollection(Collection<Pool> poolCollection) {
    this.poolCollection = poolCollection;
  }

  @Override
  public int hashCode() {
    int hash = 0;
    hash += (id != null ? id.hashCode() : 0);
    return hash;
  }

  @Override
  public boolean equals(Object object) {
    // TODO: Warning - this method won't work in the case the id fields are not set
    if (!(object instanceof Manager)) {
      return false;
    }
    Manager other = (Manager) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "com.wizards.operations.icontrol.data.Manager[ id=" + id + " ]";
  }
  
}
