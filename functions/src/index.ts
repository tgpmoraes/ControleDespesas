import {onSchedule} from "firebase-functions/v2/scheduler";
import {logger} from "firebase-functions";
import * as admin from "firebase-admin";

admin.initializeApp();

const db = admin.firestore();

export const gerarDespesasRecorrentesMensal = onSchedule(
  {
    schedule: "0 3 1 * *",
    timeZone: "America/Sao_Paulo",
    region: "us-central1",
  },
  async () => {
    logger.info("Início da execução: gerarDespesasRecorrentesMensal");

    try {
      const snapshot = await db
        .collection("despesas_recorrentes")
        .where("ativa", "==", true)
        .get();

      logger.info(`Recorrências encontradas: ${snapshot.size}`);

      if (snapshot.empty) {
        logger.info("Nenhuma recorrência ativa.");
        return;
      }

      const batch = db.batch();

      snapshot.forEach((doc) => {
        const data = doc.data();

        if (data.valor == null || data.fonteId == null) {
          logger.warn(`Recorrência ignorada: ${doc.id}`);
          return;
        }

        batch.set(db.collection("despesas").doc(), {
          classificacaoId: data.classificacaoId,
          classificacaoNome: data.classificacaoNome ?? "",
          descricao: data.descricao ?? "Despesa recorrente",
          fonteId: data.fonteId,
          valor: data.valor,

          // padrão da coleção
          parcelaAtual: 1,
          totalParcelas: 1,

          // vínculo com recorrência
          recorrenteId: doc.id,

          // data no mesmo formato das demais despesas
          data: Date.now(),

          // manter padrão atual
          id: "",
        });
      });

      await batch.commit();

      logger.info("Execução concluída com sucesso.");
    } catch (error) {
      logger.error("Erro ao executar gerarDespesasRecorrentesMensal");

      if (error instanceof Error) {
        logger.error(error.message);
      } else {
        logger.error(String(error));
      }

      return;
    }
  }
);
