import * as functions from 'firebase-functions';
import * as admin from 'firebase-admin';
admin.initializeApp()

  export const notificacaoAtualizada =  functions.firestore.document("marcacoes/{uuid}").onUpdate((snapshot, context) =>{
    const after = snapshot.after.data()
            if(after){
                if(after.estado == 'aceite'){
                    admin.firestore().doc(`pessoas/${after.personalUuid}`).get()
                        .then(snap => {
                            const payload = {          
                                data: {
                                  "tipoDeConta": "usuario",
                                  "marcacaoID": `${after.marcacaoID}`
                                },
                                notification: {
                                  "title": "marcaçao aceite",
                                  "text": `${snap.get('nome')} aceitou a sua marcaçao, pode efectuar o pagamento`
                                }
                        }
                        return admin.messaging().sendToTopic(after.usuarioUuid,payload)
                        }).catch(
                            error => {
                                console.log(error)
                            })
                }
                if(after.estado == 'paga'){
                    admin.firestore().doc(`pessoas/${after.personalUuid}`).get()
                        .then(snap => {
                    const payload = {          
                        data: {
                          "tipoDeconta": "personal"
                        },
                        notification: {
                          "title": "marcaçao paga",
                          "text":`${snap.get('nome')} efetuou o pagamento da marcaçao a sua marcaçao, pode efectuar o pagamento, ver detalhes`
                        }
                }
                return admin.messaging().sendToTopic(after.personalUuid,payload)
                }).catch(
                    error => {
                        console.log(error)
                    })
            }    
        }
                return;       
    })

    export const notificacaoCriada =  functions.firestore.document("marcacoes/{uuid}").onCreate((snapshot, context) =>{
        const marcacao = snapshot.data()
                if(marcacao){
                    const payload = {          
                        data: {
                          "tipoDeconta": "personal"
                        },
                        notification: {
                          "title": "Foi marcada uma sessao consigo",
                          "text": "Foi marcada uma sessao consigo, .",
                        }
                }
                return admin.messaging().sendToTopic(marcacao.personalUuid,payload)
                }
                    return;
        })
