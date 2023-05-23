export default {
  getThemeList(): {[key: string]: string;}[] {
    return [
      {
        value: "default",
        label: "Padrão",
      },
      {
        value: "vemmonstro",
        label: "Vem Monstro!",
      },
      {
        value: "arroz",
        label: "Arroz",
      },
    ];
  },

  getThemeName(): string {
    return localStorage.getItem("themeName") || "default";
  },

  setThemeName(themeName: string): void {
    localStorage.setItem("themeName", themeName);
  }
};