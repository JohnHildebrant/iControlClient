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
@Table(name = "user")
@XmlRootElement
@NamedQueries({
  @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
  @NamedQuery(name = "User.findById", query = "SELECT u FROM User u WHERE u.id = :id"),
  @NamedQuery(name = "User.findBySamaccountname", query = "SELECT u FROM User u WHERE u.samaccountname = :samaccountname"),
  @NamedQuery(name = "User.findByFullname", query = "SELECT u FROM User u WHERE u.fullname = :fullname")})
public class User implements Serializable {
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
  @ManyToMany(mappedBy = "userCollection")
  private Collection<Pool> poolCollection;

  public User() {
  }

  public User(Integer id) {
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
    if (!(object instanceof User)) {
      return false;
    }
    User other = (User) object;
    if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return "com.wizards.operations.icontrol.data.User[ id=" + id + " ]";
  }
  
}
