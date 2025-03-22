package com.isi.senebanque.services;

import com.isi.senebanque.dtos.requests.transaction.TransactionRequestDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.mappers.TransactionMapper;
import com.isi.senebanque.models.Compte;
import com.isi.senebanque.models.Transaction;
import com.isi.senebanque.utils.JpaUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TransactionService {
    public static final String STATUT_VALIDE = "VALIDE";
    public static final String STATUT_REJETE = "REJETE";

    public static final String TYPE_DEPOT = "DEPOT";
    public static final String TYPE_RETRAIT = "RETRAIT";
    public static final String TYPE_VIREMENT = "VIREMENT";

    private final CompteService compteService = new CompteService();

    public List<TransactionResponseDTO> getAllTransactions() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t ORDER BY t.date_transaction DESC", Transaction.class);
            List<Transaction> transactions = query.getResultList();
            return transactions.stream()
                    .map(TransactionMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<TransactionResponseDTO> getSuspectTransactions() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.statut = :statut", Transaction.class);
            query.setParameter("statut", "REJETE");
            List<Transaction> transactions = query.getResultList();
            return transactions.stream()
                    .map(TransactionMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public long getNombreTransactionsDuJour() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t WHERE t.date_transaction = CURRENT_DATE", Long.class);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }


    public long getNombreTransactionsValidees() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t WHERE t.statut = :statut", Long.class);
            query.setParameter("statut", STATUT_VALIDE);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public long getNombreTransactionsRejetees() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t WHERE t.statut = :statut", Long.class);
            query.setParameter("statut", STATUT_REJETE);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public double getMontantTotalTransactions() {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT SUM(t.montant) FROM Transaction t WHERE t.statut = :statut", Double.class);
            query.setParameter("statut", STATUT_VALIDE);
            Double resultat = query.getSingleResult();
            return resultat != null ? resultat : 0.0;
        } finally {
            entityManager.close();
        }
    }

    public TransactionResponseDTO effectuerTransaction(TransactionRequestDTO transactionDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();


            String numeroTransaction = genererNumeroTransaction();
            transactionDTO.setNumero(numeroTransaction);


            transactionDTO.setDateTransaction(new Date());


            Compte compteSource = entityManager.find(Compte.class, transactionDTO.getCompteSourceId());
            if (compteSource == null) {
                return null;
            }


            if (!"ACTIF".equals(compteSource.getStatut())) {
                Transaction transactionEchouee = new Transaction();
                transactionEchouee.setNumero(numeroTransaction);
                transactionEchouee.setType_transaction(transactionDTO.getTypeTransaction());
                transactionEchouee.setMontant(transactionDTO.getMontant());
                transactionEchouee.setDate_transaction(new Date());
                transactionEchouee.setCompte_source(compteSource);
                transactionEchouee.setStatut(STATUT_REJETE);

                entityManager.persist(transactionEchouee);
                transaction.commit();

                return TransactionMapper.toResponseDTO(transactionEchouee);
            }

            double nouveauSolde;
            String statutTransaction;


            if (TYPE_DEPOT.equals(transactionDTO.getTypeTransaction())) {

                nouveauSolde = compteSource.getSolde() + transactionDTO.getMontant();
                statutTransaction = STATUT_VALIDE;
            } else if (TYPE_RETRAIT.equals(transactionDTO.getTypeTransaction())) {

                if (compteSource.getSolde() >= transactionDTO.getMontant()) {
                    nouveauSolde = compteSource.getSolde() - transactionDTO.getMontant();
                    statutTransaction = STATUT_VALIDE;
                } else {

                    nouveauSolde = compteSource.getSolde();
                    statutTransaction = STATUT_REJETE;
                }
            } else {

                return null;
            }


            Transaction nouvelleTransaction = new Transaction();
            nouvelleTransaction.setNumero(numeroTransaction);
            nouvelleTransaction.setType_transaction(transactionDTO.getTypeTransaction());
            nouvelleTransaction.setMontant(transactionDTO.getMontant());
            nouvelleTransaction.setDate_transaction(new Date());
            nouvelleTransaction.setCompte_source(compteSource);
            nouvelleTransaction.setStatut(statutTransaction);


            if (STATUT_VALIDE.equals(statutTransaction)) {
                compteSource.setSolde(nouveauSolde);
                entityManager.merge(compteSource);
            }


            entityManager.persist(nouvelleTransaction);
            transaction.commit();

            return TransactionMapper.toResponseDTO(nouvelleTransaction);

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

    private String genererNumeroTransaction() {

        String prefixe = "TRX-";
        String date = String.format("%ty%<tm%<td", new Date());

        // Générer un nombre aléatoire à 5 chiffres
        Random random = new Random();
        String suffixe = String.format("%05d", random.nextInt(100000));

        return prefixe + date + "-" + suffixe;
    }

    public List<TransactionResponseDTO> rechercherTransactions(String terme, String statut, String type) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            String jpql = "SELECT t FROM Transaction t WHERE 1=1";

            if (statut != null && !statut.equals("Tous")) {
                jpql += " AND t.statut = :statut";
            }

            if (type != null && !type.equals("Tous")) {
                jpql += " AND t.type_transaction = :type";
            }

            if (terme != null && !terme.isEmpty()) {
                jpql += " AND (LOWER(t.numero) LIKE :terme OR CAST(t.id AS string) LIKE :terme)";
            }

            jpql += " ORDER BY t.date_transaction DESC";

            TypedQuery<Transaction> query = entityManager.createQuery(jpql, Transaction.class);

            if (statut != null && !statut.equals("Tous")) {
                query.setParameter("statut", statut);
            }

            if (type != null && !type.equals("Tous")) {
                query.setParameter("type", type);
            }

            if (terme != null && !terme.isEmpty()) {
                query.setParameter("terme", "%" + terme.toLowerCase() + "%");
            }

            List<Transaction> transactions = query.getResultList();

            return transactions.stream()
                    .map(TransactionMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }

    public List<TransactionResponseDTO> getTransactionsByCompte(Long compteId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Transaction> query = entityManager.createQuery(
                    "SELECT t FROM Transaction t WHERE t.compte_source.id = :compteId OR t.compte_dest.id = :compteId ORDER BY t.date_transaction DESC",
                    Transaction.class);
            query.setParameter("compteId", compteId);
            List<Transaction> transactions = query.getResultList();
            return transactions.stream()
                    .map(TransactionMapper::toResponseDTO)
                    .collect(Collectors.toList());
        } finally {
            entityManager.close();
        }
    }


    public long getNombreTransactionsValideesByCompte(Long compteId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t WHERE t.statut = :statut AND t.compte_source.id = :compteId OR t.compte_dest.id = :compteId", Long.class);
            query.setParameter("statut", STATUT_VALIDE);
            query.setParameter("compteId", compteId);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public long getNombreTransactionsRejeteesByCompte(Long compteId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Long> query = entityManager.createQuery(
                    "SELECT COUNT(t) FROM Transaction t WHERE t.statut = :statut AND t.compte_source.id = :compteId OR t.compte_dest.id = :compteId", Long.class);
            query.setParameter("statut", STATUT_REJETE);
            query.setParameter("compteId", compteId);
            return query.getSingleResult();
        } finally {
            entityManager.close();
        }
    }

    public double getMontantTotalTransactionsByCompte(Long compteId) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        try {
            TypedQuery<Double> query = entityManager.createQuery(
                    "SELECT SUM(t.montant) FROM Transaction t WHERE t.statut = :statut AND t.compte_source.id = :compteId OR t.compte_dest.id = :compteId", Double.class);
            query.setParameter("statut", STATUT_VALIDE);
            query.setParameter("compteId", compteId);
            Double resultat = query.getSingleResult();
            return resultat != null ? resultat : 0.0;
        } finally {
            entityManager.close();
        }
    }

    public TransactionResponseDTO effectuerVirement(TransactionRequestDTO transactionDTO) {
        EntityManager entityManager = JpaUtil.getEntityManager();
        EntityTransaction transaction = entityManager.getTransaction();

        try {
            transaction.begin();

            // Générer le numéro de transaction
            String numeroTransaction = genererNumeroTransaction();
            transactionDTO.setNumero(numeroTransaction);

            // Définir la date de transaction à maintenant
            transactionDTO.setDateTransaction(new Date());

            // Récupérer les comptes source et destination
            Compte compteSource = entityManager.find(Compte.class, transactionDTO.getCompteSourceId());
            Compte compteDest = entityManager.find(Compte.class, transactionDTO.getCompteDestId());

            if (compteSource == null || compteDest == null) {
                return null;
            }

            // Vérifier que les comptes sont actifs
            if (!"ACTIF".equals(compteSource.getStatut()) || !"ACTIF".equals(compteDest.getStatut())) {
                Transaction transactionEchouee = new Transaction();
                transactionEchouee.setNumero(numeroTransaction);
                transactionEchouee.setType_transaction(TYPE_VIREMENT);
                transactionEchouee.setMontant(transactionDTO.getMontant());
                transactionEchouee.setDate_transaction(new Date());
                transactionEchouee.setCompte_source(compteSource);
                transactionEchouee.setCompte_dest(compteDest);
                transactionEchouee.setStatut(STATUT_REJETE);

                entityManager.persist(transactionEchouee);
                transaction.commit();

                return TransactionMapper.toResponseDTO(transactionEchouee);
            }

            // Vérifier si le solde est suffisant
            if (compteSource.getSolde() < transactionDTO.getMontant()) {
                // Solde insuffisant, la transaction est rejetée
                Transaction transactionEchouee = new Transaction();
                transactionEchouee.setNumero(numeroTransaction);
                transactionEchouee.setType_transaction(TYPE_VIREMENT);
                transactionEchouee.setMontant(transactionDTO.getMontant());
                transactionEchouee.setDate_transaction(new Date());
                transactionEchouee.setCompte_source(compteSource);
                transactionEchouee.setCompte_dest(compteDest);
                transactionEchouee.setStatut(STATUT_REJETE);

                entityManager.persist(transactionEchouee);
                transaction.commit();

                return TransactionMapper.toResponseDTO(transactionEchouee);
            }

            // Effectuer le virement
            compteSource.setSolde(compteSource.getSolde() - transactionDTO.getMontant());
            compteDest.setSolde(compteDest.getSolde() + transactionDTO.getMontant());

            // Créer la transaction
            Transaction nouvelleTransaction = new Transaction();
            nouvelleTransaction.setNumero(numeroTransaction);
            nouvelleTransaction.setType_transaction(TYPE_VIREMENT);
            nouvelleTransaction.setMontant(transactionDTO.getMontant());
            nouvelleTransaction.setDate_transaction(new Date());
            nouvelleTransaction.setCompte_source(compteSource);
            nouvelleTransaction.setCompte_dest(compteDest);
            nouvelleTransaction.setStatut(STATUT_VALIDE);

            entityManager.merge(compteSource);
            entityManager.merge(compteDest);
            entityManager.persist(nouvelleTransaction);

            transaction.commit();

            return TransactionMapper.toResponseDTO(nouvelleTransaction);

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
}
