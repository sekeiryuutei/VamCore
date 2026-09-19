/**
 * Variables de entorno de PRODUCCIÓN.
 * `apiBaseUrl` normalmente se sobreescribe en el pipeline de build/deploy
 * (por ejemplo vía reemplazo de este archivo o variables inyectadas en el
 * contenedor Docker del frontend).
 */
export const environment = {
  production: true,
  apiBaseUrl: '/api/v1',
  platformName: 'VamCore',
  companyName: 'CodeVam',
  productNames: {
    inventory: 'VamStock',
    assets: 'VamAsset',
    logistics: 'VamTrack',
  },
};
