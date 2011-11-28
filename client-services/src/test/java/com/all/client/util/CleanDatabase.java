package com.all.client.util;


// understands how to clean all data from the database
public class CleanDatabase {
	public static void main(String[] args) {
		CleanDatabase cleanDatabase = new CleanDatabase();
		cleanDatabase.cleanAll();
	}

	public void cleanAll() {
		// Session session = HibernateUtil.getSession();
		// deleteAll(session, "from Playlist as Playlist");
		// deleteAll(session, "from PlaylistTrack as PlaylistTrack");
		// deleteAll(session, "from Track as Track");
		// deleteAll(session, "from Folder as Folder");
		// deleteAll(session, "from Genre as Genre");
	}

	// private void deleteAll(Session session, String query) {
	// List toDelete;
	// toDelete = (List) session.createQuery(query).list();
	// DomainEntity.deleteAll(toDelete);
	// }

}
