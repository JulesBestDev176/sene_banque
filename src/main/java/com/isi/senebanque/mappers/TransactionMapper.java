package com.isi.senebanque.mappers;

import com.isi.senebanque.dtos.requests.transaction.TransactionRequestDTO;
import com.isi.senebanque.dtos.responses.transaction.TransactionResponseDTO;
import com.isi.senebanque.models.Transaction;

public class TransactionMapper {


    public static Transaction toEntity(TransactionRequestDTO transactionRequestDTO) {
        return Transaction.builder()
                .numero(transactionRequestDTO.getNumero())
                .type_transaction(transactionRequestDTO.getTypeTransaction())
                .montant(transactionRequestDTO.getMontant())
                .date_transaction(transactionRequestDTO.getDateTransaction())
                .statut(transactionRequestDTO.getStatut())
                .build();
    }


    public static TransactionResponseDTO toResponseDTO(Transaction transaction) {
        return TransactionResponseDTO.builder()
                .id(transaction.getId())
                .numero(transaction.getNumero())
                .typeTransaction(transaction.getType_transaction())
                .montant(transaction.getMontant())
                .dateTransaction(transaction.getDate_transaction())
                .compteSourceId(transaction.getCompte_source() != null ? transaction.getCompte_source().getId() : null)
                .compteDestId(transaction.getCompte_dest() != null ? transaction.getCompte_dest().getId() : null)
                .statut(transaction.getStatut())
                .build();
    }


    public static void updateEntityFromDTO(TransactionRequestDTO transactionRequestDTO, Transaction transaction) {
        transaction.setNumero(transactionRequestDTO.getNumero());
        transaction.setType_transaction(transactionRequestDTO.getTypeTransaction());
        transaction.setMontant(transactionRequestDTO.getMontant());
        transaction.setDate_transaction(transactionRequestDTO.getDateTransaction());
        transaction.setStatut(transactionRequestDTO.getStatut());
    }
}
