package com.ettrema.http.caldav.demo;

import com.bradmcevoy.common.Path;
import com.bradmcevoy.http.Resource;
import com.bradmcevoy.http.ResourceFactory;
import com.bradmcevoy.http.values.HrefList;
import java.util.Calendar;

/**
 * For iCal, start off by opening a calendar at
 *
 * http://localhost:8080/users/userA/  - iCal will discover the calendar inside
 * that user.
 *
 * For Mozilla clients (eg thunderbird) connect directory to the calendar url, eg
 *
 * http://localhost:8080/users/userA/calendars/cal1/
 *
 * @author brad
 */
public class TResourceFactory implements ResourceFactory {

    private static org.apache.log4j.Logger log = org.apache.log4j.Logger.getLogger( TResourceFactory.class );
    public static final TFolderResource ROOT = new TFolderResource( (TFolderResource) null, "http://localhost:8080" );

    static TFolderResource users;
	static TFolderResource principals;

    static {
		principals = new TFolderResource(ROOT, "principals");
		users = principals; // same as sabresav demo
        //users = new TFolderResource( principals, "users" );        
        addUser(users, "userA","password");
        addUser(users, "userB","password");
        addUser(users, "userC","password");
    }

    private static void addUser(TFolderResource users, String name, String password) {
        TCalDavPrincipal userA = new TCalDavPrincipal(users, name,password, null, null, null, null, null);
        TFolderResource calendars = new TFolderResource(userA, "calendars");		
        TCalendarResource cal1 = new TCalendarResource(calendars, "cal1");
        TEvent e = new TEvent(cal1, "event1.ics");
        e.setiCalData(createICalData());
		
		TFolderResource addressBooks = new TFolderResource(userA, "addressBooks");
		userA.setAddressBookHome(addressBooks);
		TAddressBookResource addressBook1 = new TAddressBookResource(addressBooks, "addressbook");
		System.out.println("created address book: " + addressBook1.getHref());
		TContact c1 = new TContact(addressBook1, "contact1.vcf");
		c1.setData(getCardDavData("111222333"));
		
        TScheduleInboxResource scheduleInbox = new TScheduleInboxResource(calendars, "inbox");
        TScheduleOutboxResource scheduleOutbox = new TScheduleOutboxResource(calendars, "outbox");
        userA.setCalendarHome(calendars);
        userA.setScheduleInboxResource(scheduleInbox);
        userA.setScheduleOutboxResource(scheduleOutbox);
    }

    public static TCalDavPrincipal findUser(String name) {
        if( name.contains("@")) {
            name = name.substring(0, name.indexOf("@"));
        }
        System.out.println("find user:" + name);
        for(Resource r : users.children) {
            if( r.getName().equals(name)) {
                return (TCalDavPrincipal) r;
            }
        }
        return null;
    }

    static HrefList getPrincipalCollectionHrefs() {
        HrefList list = new HrefList();
        list.add("/users/");
        return list;
    }

	@Override
    public Resource getResource( String host, String url ) {
        log.debug( "getResource: url: " + url );
        Path path = Path.path( url );
        Resource r = find( path );
        log.debug( "_found: " + r + " for url: " + url + " and path: " + path );
        return r;
    }

    private Resource find( Path path ) {
        if( path.isRoot() ) {
            return ROOT;
        }
        Resource r = find( path.getParent() );
        if( r == null ) return null;
        if( r instanceof TFolderResource ) {
            TFolderResource folder = (TFolderResource) r;
            for( Resource rChild : folder.getChildren() ) {
                Resource r2 = rChild;
                if( r2.getName().equals( path.getName() ) ) {
                    return r2;
                }
            }
        }
        log.debug( "not found: " + path );
        return null;
    }


    private static String createICalData() {
        Calendar cal = Calendar.getInstance();

        String start = format(cal);
        cal.add(Calendar.HOUR, 2);
        String finish = format(cal);

        String s = "";
        s+= "BEGIN:VCALENDAR\n";
        s+= "PRODID:-//MailEnable.com MailEnable Calendar V1.1//EN\n";
        s+= "VERSION:2.0\n";
        s+= "METHOD:PUBLISH\n";
        s+= "BEGIN:VTIMEZONE\n";
        s+= "TZID:America/New_York\n";
        s+= "X-LIC-LOCATION:America/New_York\n";
        s+= "BEGIN:DAYLIGHT\n";
        s+= "TZOFFSETFROM:-0500\n";
        s+= "TZOFFSETTO:-0400\n";
        s+= "TZNAME:EDT\n";
        s+= "DTSTART:19700308T020000\n";
        s+= "RRULE:FREQ=YEARLY;BYDAY=2SU;BYMONTH=3\n";
        s+= "END:DAYLIGHT\n";
        s+= "BEGIN:STANDARD\n";
        s+= "TZOFFSETFROM:-0400\n";
        s+= "TZOFFSETTO:-0500\n";
        s+= "TZNAME:EST\n";
        s+= "DTSTART:19701101T020000\n";
        s+= "RRULE:FREQ=YEARLY;BYDAY=1SU;BYMONTH=11\n";
        s+= "END:STANDARD\n";
        s+= "END:VTIMEZONE\n";
        s+= "BEGIN:VEVENT\n";
        s+= "CREATED:20091113T212858Z\n";
        s+= "LAST-MODIFIED:20090814T231840Z\n";
        s+= "DTSTAMP:20090814T231840Z\n";
        s+= "UID:0C4DBFA762A44E359A373562C9DE463A.CAL\n";
        s+= "SUMMARY:consona\n";
        s+= "PRIORITY:5\n";
        s+= "ORGANIZER:mailto:vvvvv@zzzz.com\n";
        s+= "DTSTART:" + start + "\n";
        s+= "DTEND:" + finish + "\n";
        s+= "CLASS:PUBLIC\n";
        s+= "TRANSP:OPAQUE\n";
        s+= "SEQUENCE:0\n";
        s+= "X-MICROSOFT-CDO-ALLDAYEVENT:FALSE\n";
        s+= "X-MICROSOFT-CDO-IMPORTANCE:0\n";
        s+= "X-MICROSOFT-CDO-BUSYSTATUS:1\n";
        s+= "END:VEVENT\n";
        s+= "END:VCALENDAR\n";
        return s;
    }
	
	private static String getCardDavData(String phone) {
		String s = "";
		s += "BEGIN:VCARD\n";
		s += "VERSION:3.0\n";
		s += "N:test;test;;;\n";
		s += "FN:test test\n";
		s += "TEL;type=WORK;type=pref:" + phone + "\n";
		s += "item1.ADR;type=WORK;type=pref:;;\n\n;;;;\n";
		s += "item1.X-ABADR:nz\n";
		s += "CATEGORIES:My Contacts\n";
		s += "X-ABUID:603CDA60-383D-4DD4-A478-432533516501\\:ABPerson\n";
		s += "UID:95490BEA-5793-4E3B-8788-054C8B394F68-ABSPlugin\n";
		s += "REV:2011-12-15T01:04:25Z\n";
		s += "END:VCARD\n";
		return s;
	}

    private static String format(Calendar cal) {
        // "20090820T180000Z";
        String s = "" + cal.get(Calendar.YEAR);
        s += pad2(cal.get(Calendar.MONTH)+1);
        s += pad2(cal.get(Calendar.DATE));
        s += "T";
        s += pad2(cal.get(Calendar.HOUR_OF_DAY));
        s += pad2(cal.get(Calendar.MINUTE));
        s += pad2(cal.get(Calendar.SECOND));
        s += "Z";
        return s;
    }

    private static String pad2(int i) {
        if( i < 10) {
            return "0" + i;
        } else {
            return i + "";
        }
    }
}
