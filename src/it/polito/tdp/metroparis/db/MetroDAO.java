package it.polito.tdp.metroparis.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.javadocmd.simplelatlng.LatLng;

import it.polito.tdp.metroparis.model.Fermata;
import it.polito.tdp.metroparis.model.Linea;

public class MetroDAO {

	public List<Fermata> getAllFermate() {

		final String sql = "SELECT id_fermata, nome, coordx, coordy FROM fermata ORDER BY nome ASC";
		List<Fermata> fermate = new ArrayList<Fermata>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Fermata f = new Fermata(rs.getInt("id_Fermata"), rs.getString("nome"),
						new LatLng(rs.getDouble("coordx"), rs.getDouble("coordy")));
				fermate.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return fermate;
	}

	public List<Linea> getAllLinee() {
		final String sql = "SELECT id_linea, nome, velocita, intervallo FROM linea ORDER BY nome ASC";

		List<Linea> linee = new ArrayList<Linea>();

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Linea f = new Linea(rs.getInt("id_linea"), rs.getString("nome"), rs.getDouble("velocita"),
						rs.getDouble("intervallo"));
				linee.add(f);
			}

			st.close();
			conn.close();

		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		return linee;
	}
    /**
     * PRIMA SOLUZIONE CON I GRAFI, NON CONSIGLIATA, SPESSO NON FUNZIONA
     * @param partenza verice di partenza
     * @param arrivo vertice di arrivo
     * @return un valore numerico  che ci dice se esiste un collegamento tra i due vertici
     *         
     */
    public boolean esisteConnessione(Fermata partenza, Fermata arrivo) {
		
    	final String sql = "SELECT COUNT(*) AS cnt " + 
    			"FROM connessione " + 
    			"WHERE id_stazP=? " + 
    			"AND id_stazA=?";
    	try{
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getIdFermata()); //impostiamo i parametri
			st.setInt(2, arrivo.getIdFermata());
			
			ResultSet rs = st.executeQuery();

		    rs.next(); // mi posiziono sulla prima (e unica) riga
		    
		    int numero = rs.getInt("cnt");

//			st.close();
			conn.close();
//			se il numero è maggiore di zero esiste una connessione, altrimenti
//			è uguale a zero e non esiste connessione, in questo caso ritorna "false"
			return  (numero>0); 

		} catch (SQLException e) {
			e.printStackTrace();
			
		}
        return false;
	}

    /**
     *  SECONDA SOLUZIONE
     * @param partenza
     * @return
     */
	public List<Fermata> stazioniArrivo(Fermata partenza, Map<Integer, Fermata> idMap) {
		
		final String sql = "SELECT id_stazA " + 
    			           "FROM connessione " + 
    			           "WHERE id_stazP=?" ;
		
		Connection conn = DBConnect.getConnection();
		try {
				PreparedStatement st = conn.prepareStatement(sql);
				st.setInt(1, partenza.getIdFermata());
				ResultSet rs = st.executeQuery();
				
				List<Fermata> result = new ArrayList<>();

				while(rs.next()) {
//		PASSIAMO LA idMap E LA USIAMO PER RECUPERARE GLI OGGETTI DA AGGIUNGERE
//		ALLA LISTA RESULT SENZA DOVER CREARE NUOVI OGGETTI
//	-----> Grazie alla idMap creiamo gli oggetti una sola volta e pooi ad ogni metodo
//		che debba usarli passiamo la identityMap come parametro
					result.add(idMap.get(rs.getInt("id_stazA")));
				}
				
				conn.close();
				return result;
				
			} catch (SQLException e) {
				
				e.printStackTrace();
			
			}
					
		return null;
	}

}
