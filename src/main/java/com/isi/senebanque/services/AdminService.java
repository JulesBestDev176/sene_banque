package com.isi.senebanque.services;

import com.isi.senebanque.models.Admin;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class AdminService {


    public List<Admin> getAllAdmins() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                    "SELECT a FROM Admin a ORDER BY a.nom, a.prenom", Admin.class);
            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }


    public Admin getAdminById(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            return entityManager.find(Admin.class, id);
        } finally {
            entityManager.close();
        }
    }


    public Admin getAdminByUsername(String username) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                    "SELECT a FROM Admin a WHERE a.username = :username", Admin.class);
            query.setParameter("username", username);
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            entityManager.close();
        }
    }


    public Admin authentifier(String username, String password) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Admin> query = entityManager.createQuery(
                    "SELECT a FROM Admin a WHERE a.username = :username AND a.password = :password", Admin.class);
            query.setParameter("username", username);
            query.setParameter("password", password);

            try {
                return query.getSingleResult();
            } catch (NoResultException e) {
                return null;
            }
        } finally {
            entityManager.close();
        }
    }


    public Admin ajouterAdmin(Admin admin) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.persist(admin);
            transaction.commit();
            return admin;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            entityManager.close();
        }
    }


    public boolean modifierAdmin(Admin admin) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            entityManager.merge(admin);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            entityManager.close();
        }
    }

    public boolean supprimerAdmin(Long id) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Admin admin = entityManager.find(Admin.class, id);
            if (admin != null) {
                entityManager.remove(admin);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            entityManager.close();
        }
    }


    public boolean changerMotDePasse(Long adminId, String nouveauPassword) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            Admin admin = entityManager.find(Admin.class, adminId);
            if (admin != null) {
                admin.setPassword(nouveauPassword);
                entityManager.merge(admin);
                transaction.commit();
                return true;
            } else {
                transaction.rollback();
                return false;
            }
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        } finally {
            entityManager.close();
        }
    }


    public boolean isUsernameExistant(String username) {
        return getAdminByUsername(username) != null;
    }


    public boolean isEmailExistant(String email, Long adminIdExclu) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT COUNT(a) FROM Admin a WHERE a.email = :email";
            if (adminIdExclu != null) {
                jpql += " AND a.id != :adminIdExclu";
            }

            TypedQuery<Long> query = entityManager.createQuery(jpql, Long.class);
            query.setParameter("email", email);

            if (adminIdExclu != null) {
                query.setParameter("adminIdExclu", adminIdExclu);
            }

            return query.getSingleResult() > 0;
        } finally {
            entityManager.close();
        }
    }


    public long getNombreTotalAdmins() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(a) FROM Admin a", Long.class);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public List<Admin> rechercherAdmins(String terme) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            StringBuilder jpql = new StringBuilder("SELECT a FROM Admin a WHERE 1=1");

            if (terme != null && !terme.isEmpty()) {
                jpql.append(" AND (LOWER(a.nom) LIKE :terme OR LOWER(a.prenom) LIKE :terme OR LOWER(a.email) LIKE :terme OR LOWER(a.username) LIKE :terme)");
            }

            jpql.append(" ORDER BY a.nom, a.prenom");

            TypedQuery<Admin> query = entityManager.createQuery(jpql.toString(), Admin.class);

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            return query.getResultList();
        } finally {
            entityManager.close();
        }
    }
}
