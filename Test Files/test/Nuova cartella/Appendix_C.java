package ExternalClasse;

public class Appendix_C {

	@Nullable
	private MethodJavaSymbol overriddenSymbolFrom(ClassJavaType classType) {
		if (classType.isUnknown()) { 															// +1
			return Symbols.unknownMethodSymbol;
		}
	boolean unknownFound = false;
	
	List<JavaSymbol> symbols = classType.getSymbol().members().lookup(name);
	for (JavaSymbol overrideSymbol : symbols) { 												// +1
		if (overrideSymbol.isKind(JavaSymbol.MTH) 												// +2 (nesting = 1)
				&& !overrideSymbol.isStatic()) { 												// +1
			MethodJavaSymbol methodJavaSymbol = (MethodJavaSymbol)overrideSymbol;
			if (canOverride(methodJavaSymbol)) { 												// +3 (nesting = 2)
				Boolean overriding = checkOverridingParameters(methodJavaSymbol, classType);
				if (overriding == null) { 														// +4 (nesting = 3)
					if (!unknownFound) { 														// +5 (nesting = 4)
						unknownFound = true;
					}
				} else if (overriding) {														 // +1
					return methodJavaSymbol;
				}
			}
		}
	}
	if (unknownFound) { 																		// +1
		return Symbols.unknownMethodSymbol;
	}
	return null;
	} 																							// total complexity = 19

	private void addVersion(final Entry entry, final Transaction txn) throws PersistitInterruptedException, RollbackException {
		final TransactionIndex ti = _persistit.getTransactionIndex();
		while (true) { 																						// +1
			try {
				synchronized (this) {
					if (frst != null) { 																	// +2 (nesting = 1)
						if (frst.getVersion() > entry.getVersion()) {										// +3 (nesting = 2)
							throw new RollbackException();
						}
						if (txn.isActive()) { 																// +3 (nesting = 2)
							for 																			// +4 (nesting = 3)
							(Entry e = frst; e != null; e = e.getPrevious()) {
								final long version = e.getVersion();
								final long depends = ti.wwDependency(version,
										txn.getTransactionStatus(), 0);
								if (depends == TIMED_OUT) { 												// +5 (nesting = 4)
									throw new WWRetryException(version);
								}
								if (depends != 0 															// +5 (nesting = 4)
										&& depends != ABORTED) { 											// +1
									throw new RollbackException();
								}
							}
						}
					}
					entry.setPrevious(frst);
					frst = entry;
					break;
				}
			} catch (final WWRetryException re) {															// +2 (nesting = 1)
				try {
					final long depends = _persistit.getTransactionIndex()
							.wwDependency(re.getVersionHandle(),txn.getTransactionStatus(),
									SharedResource.DEFAULT_MAX_WAIT_TIME);
					if (depends != 0 																		// +3 (nesting = 2)
							&& depends != ABORTED) { 														// +1
						throw new RollbackException();
					}
				} catch (final InterruptedException ie) { 													// +3 (nesting = 2)
					throw new PersistitInterruptedException(ie);
				}
			} catch (final InterruptedException ie) { 														// +2 (nesting = 1)
				throw new PersistitInterruptedException(ie);
			}
		}
	}																									// total complexity = 35
	 
	private static String toRegexp(String antPattern, String directorySeparator) {
		final String escapedDirectorySeparator = '\\' + directorySeparator;
		final StringBuilder sb = new StringBuilder(antPattern.length());
		
		sb.append('^');
		
		int i = antPattern.startsWith("/") || 													// +1
		antPattern.startsWith("\\") ? 1 : 0; 													// +1
		
		while (i < antPattern.length()) { 														// +1
			final char ch = antPattern.charAt(i);
			if (SPECIAL_CHARS.indexOf(ch) != -1) { 												// +2 (nesting = 1)
				sb.append('\\').append(ch);
			} else if (ch == '*') { 															// +1
				if (i + 1 < antPattern.length() 												// +3 (nesting = 2)
						&& antPattern.charAt(i + 1) == '*') { 									// +1
					if (i + 2 < antPattern.length() 											// +4 (nesting = 3)
							&& isSlash(antPattern.charAt(i + 2))) { 							// +1
						sb.append("(?:.*")
						.append(escapedDirectorySeparator).append("|)");
						i += 2;
					} else { 																	// +1
						sb.append(".*");
						i += 1;
					}
				} else { 																		// +1
					sb.append("[^").append(escapedDirectorySeparator).append("]*?");
				}
			} else if (ch == '?') { 															// +1
				sb.append("[^").append(escapedDirectorySeparator).append("]");
			} else if (isSlash(ch)) { 															// +1
				sb.append(escapedDirectorySeparator);
			} else { // +1
				sb.append(ch);
			}
			i++;
		}
			sb.append('$');
			return sb.toString();
	} 																							// total complexity = 20

}
