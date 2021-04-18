
public class MainClass {

	int intAttr=0;

	public void method1a() {
		int i=0;
		System.out.println("method1c: inizio");
		while(i<10) {
			System.out.println("method1c: nel loop con i="+i);
			if(i>4) {
				System.out.println("break con i="+i);
				break;
			}
			i++;
		}
		System.out.println("method1c: esco con i="+i);
	}
	
	public void method1b() {
		int i=0;
		System.out.println("method1c: inizio");
		pluto:
		while(i<10) {
			System.out.println("method1c: nel loop con i="+i);
			if(i>4) {
				System.out.println("break con i="+i);
				break pluto;
			}
			i++;
		}
		System.out.println("method1c: esco con i="+i);
	}


	public void method2a() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		
		if(a && b && c || d ) {
			k+=99;
		}			
	}

	public void method2b() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j, cond=false;
		cond=a && b && c || d;
		if(cond) {
			k+=99;
		}			
	}
	
	public void method3a(MainClass o1) {
		MainClass local=new MainClass();
		if(o1 != null) {
			local= o1;
		}
	}

	public void method3b(MainClass o1) {
		MainClass local=new MainClass();
		local= (o1 != null) ? o1: local;
	}

	public void method4a() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		if(a && b) {
			k+=99;
		}			
	}
	
	public void method4b() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		if(a && !b) {
			k+=99;
		}			
	}

	public void method5a() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		if(a) {
			k+=99;
		}			
	}
	
	public void method5b() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		if(!a) {
			k+=99;
		}			
	}

	public void method5c() {
		int i=1, j=2, k=3;
		boolean a=i==j, b=j<k, c=k>i+j, d=i+1==j;
		if(!!!!!!!a) {
			k+=99;
		}			
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int z;
		MainClass theObject=new MainClass();
		theObject.method2a();
		theObject.method2b();
		// z = theObject?.intAttr; // not implemented in Java
		theObject.method1a();
		theObject.method1b();

	}

}
